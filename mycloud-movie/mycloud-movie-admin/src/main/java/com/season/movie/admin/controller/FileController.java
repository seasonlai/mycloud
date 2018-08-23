package com.season.movie.admin.controller;

import com.season.common.base.BaseException;
import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.web.util.WebFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by Administrator on 2018/8/18.
 */
@Api("文件上传下载")
@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(FileController.class);


    @Deprecated
    @ApiIgnore
    @GetMapping("/upload")
    public ModelAndView uploadTest() {
        return new ModelAndView("uploadTest");
    }

    @Deprecated
    @ApiIgnore
    @GetMapping("/getFileOffset")
    public BaseResult uploadOffset(String fileName, HttpServletRequest request) {
        File file = new File(WebFileUtils.getVideoFileDir(request), fileName);
        if (file.exists()) {
            return BaseResult.successData(file.length());
        }
        return BaseResult.result(ResultCode.ERROR, "文件不存在");
    }

    @ApiOperation(value = "上传图片", httpMethod = "POST")
    @PostMapping("/uploadImg")
    public BaseResult uploadMovieImg(@RequestParam("imgData") String imgData,
                                     @RequestParam("filename") String filename,
                                     HttpServletRequest request) {
        String url = null;
        File tmpFileDir = WebFileUtils.getTmpFileDir(request);
        File file = WebFileUtils.getTmpFileDir(request);
        if (Objects.isNull(file)) {
            return new BaseResult(ResultCode.IO_ERROR, "获取临时路径失败");
        }
        if (!filename.contains(".")) {
            return new BaseResult(ResultCode.IO_ERROR, "文件格式不对");
        }
        filename = WebFileUtils.getFileNameWithTimestamp(filename);
        file = new File(tmpFileDir, filename);
        String base64 = imgData.substring(imgData.indexOf(",") + 1);
        byte[] decoderBytes = Base64.getDecoder().decode(base64);
        try {
            FileUtils.writeByteArrayToFile(file, decoderBytes);
        } catch (IOException e) {
            logger.error("写文件失败", e);
            return new BaseResult(ResultCode.IO_ERROR, "上传文件失败");
        }
        //返回图片访问路径
        url = request.getSession().getServletContext().getAttribute("_tmpPath") + filename;
        return BaseResult.successData(url);

    }


    @PostMapping("/uploadFilePair")
    public BaseResult upload(HttpServletRequest request,
                             @RequestParam(value = "fileName", required = false) String fileName,
                             HttpServletResponse response) throws Exception {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取传入文件
        multipartRequest.setCharacterEncoding("utf-8");
        Iterator<String> fileNames = multipartRequest.getFileNames();
        MultipartFile file = multipartRequest.getFile("file");

        if (Objects.isNull(file)) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            throw new BaseException(ResultCode.VALIDATE_ERROR, "文件内容为空");
        }
        //生成文件名
        if (Objects.isNull(fileName)) {
            String originName = file.getOriginalFilename();
//            if (StringUtils.isEmpty(originName) || !originName.contains(".")) {
//                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
//                throw new BaseException(ResultCode.VALIDATE_ERROR, "文件名格式不支持");
//            }
            int index = originName.lastIndexOf(".");
            String suffix = "";
            if (index >= 0) {
                suffix = originName.substring(index);
            }
            fileName = WebFileUtils.getFileNameWithTimestamp(UUID.randomUUID().toString() + suffix);
        }

        File videoFileDir = WebFileUtils.getVideoFileDir(request);
        if (Objects.isNull(videoFileDir)) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "文件保存路径获取失败");
        }
        this.SaveAs(new File(videoFileDir, fileName), file, request);
        // 设置返回值
        return BaseResult.successData(fileName);
    }


    private void SaveAs(File saveFile, MultipartFile file,
                        HttpServletRequest request) throws Exception {

        long lStartPos = 0;
        long startPosition = 0;
        long endPosition = 0;
        String contentRange = request.getHeader("Content-Range");
        if (logger.isDebugEnabled()) {
            logger.debug("上传文件：{} - {}", saveFile.getName(), contentRange);
        }
        if (StringUtils.isEmpty(contentRange)) {
            FileUtils.copyInputStreamToFile(file.getInputStream(), saveFile);
        } else {
            // bytes 10000-19999/1157632     将获取到的数据进行处理截取出开始跟结束位置
            contentRange = contentRange.replace("bytes", "").trim();
            contentRange = contentRange.substring(0,
                    contentRange.indexOf("/"));
            String[] ranges = contentRange.split("-");
            startPosition = Integer.parseInt(ranges[0]);
            endPosition = Integer.parseInt(ranges[1]);

            //判断所上传文件是否已经存在，若存在则返回存在文件的大小
            RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rw");
            lStartPos = accessFile.length();

            //判断所上传文件片段是否存在，若存在则直接返回
            if (lStartPos > endPosition) {
                accessFile.close();
                return;
            }
            //要写文件
            long needRead;
            if (lStartPos < startPosition) {
                accessFile.seek(startPosition);
                needRead = endPosition - startPosition;
            } else {
                accessFile.seek(lStartPos);
                file.getInputStream().skip(lStartPos);
                needRead = endPosition - lStartPos;
            }
            byte[] data = new byte[(int) needRead];
            try {
                int count = file.getInputStream().read(data);
                if(count!=-1){
                    accessFile.write(data, 0, count);
                }
            } finally {
                accessFile.close();
            }
        }

    }


}
