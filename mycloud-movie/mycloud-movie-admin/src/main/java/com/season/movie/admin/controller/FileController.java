package com.season.movie.admin.controller;

import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.web.util.WebFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/18.
 */
@Api("文件上传下载")
@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(FileController.class);

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

    @RequestMapping("/uploadFilePair")
    public BaseResult upload(HttpServletRequest request, PrintWriter writer,
                       HttpServletResponse response) throws Exception {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取传入文件
        multipartRequest.setCharacterEncoding("utf-8");
        MultipartFile file = multipartRequest.getFile("file");

        this.SaveAs("uploadDemo/" + file.getOriginalFilename(), file, request,
                response);
        // 设置返回值
        return BaseResult.successData(file.getOriginalFilename());
    }

    private void SaveAs(String saveFilePath, MultipartFile file,
                        HttpServletRequest request, HttpServletResponse response) throws Exception {

        long lStartPos = 0;
        int startPosition = 0;
        int endPosition = 0;
        int fileLength = 100000;
        OutputStream fs = null;

        String contentRange = request.getHeader("Content-Range");
        System.out.println(contentRange);
        if (!new File("uploadDemo").exists()) {
            new File("uploadDemo").mkdirs();
        }
        if (contentRange == null) {
            FileUtils.writeByteArrayToFile(new File(saveFilePath),
                    file.getBytes());

        } else {
            // bytes 10000-19999/1157632     将获取到的数据进行处理截取出开始跟结束位置
            if (contentRange != null && contentRange.length() > 0) {
                contentRange = contentRange.replace("bytes", "").trim();
                contentRange = contentRange.substring(0,
                        contentRange.indexOf("/"));
                String[] ranges = contentRange.split("-");
                startPosition = Integer.parseInt(ranges[0]);
                endPosition = Integer.parseInt(ranges[1]);
            }

            //判断所上传文件是否已经存在，若存在则返回存在文件的大小
            if (new File(saveFilePath).exists()) {
                fs = new FileOutputStream(saveFilePath, true);
                FileInputStream fi = new FileInputStream(saveFilePath);
                lStartPos = fi.available();
                fi.close();
            } else {
                fs = new FileOutputStream(saveFilePath);
                lStartPos = 0;
            }

            //判断所上传文件片段是否存在，若存在则直接返回
            if (lStartPos > endPosition) {
                fs.close();
                return;
            } else if (lStartPos < startPosition) {
                byte[] nbytes = new byte[fileLength];
                int nReadSize = 0;
                file.getInputStream().skip(startPosition);
                nReadSize = file.getInputStream().read(nbytes, 0, fileLength);
                if (nReadSize > 0) {
                    fs.write(nbytes, 0, nReadSize);
                    nReadSize = file.getInputStream().read(nbytes, 0,
                            fileLength);
                }
            } else if (lStartPos > startPosition && lStartPos < endPosition) {
                byte[] nbytes = new byte[fileLength];
                int nReadSize = 0;
                file.getInputStream().skip(lStartPos);
                int end = (int) (endPosition - lStartPos);
                nReadSize = file.getInputStream().read(nbytes, 0, end);
                if (nReadSize > 0) {
                    fs.write(nbytes, 0, nReadSize);
                    nReadSize = file.getInputStream().read(nbytes, 0, end);
                }
            }
        }
        if (fs != null) {
            fs.flush();
            fs.close();
            fs = null;
        }

    }


}
