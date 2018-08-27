package com.season.movie.admin.controller;

import com.season.common.base.BaseException;
import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.util.FileMd5Util;
import com.season.common.web.util.WebFileUtils;
import com.season.movie.admin.form.ImageForm;
import com.season.movie.dao.entity.FileInfo;
import com.season.movie.dao.enums.TaskStatus;
import com.season.movie.service.service.FileInfoService;
import com.season.movie.service.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
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

    @Autowired
    FileInfoService fileInfoService;
    @Autowired
    TaskService taskService;

    @Deprecated
    @ApiIgnore
    @GetMapping("/upload")
    public ModelAndView uploadTest() {
        return new ModelAndView("uploadTest");
    }

    @Deprecated
    @ApiIgnore
    @GetMapping("/getFileOffset")
    public BaseResult getFileInfo(@RequestParam("fileName") String fileName,
                                  HttpServletRequest request,
                                  @RequestParam(value = "md5", required = false) String md5) {

        if (!StringUtils.isEmpty(md5)) {
            FileInfo info = fileInfoService.findByMd5(md5);
            if (!Objects.isNull(info)) {
                return BaseResult.success(info.getName(), -2);//文件已上传过了
            }
        }
        File file = new File(WebFileUtils.getVideoFileDir(request), fileName);
        if (file.exists()) {
            return BaseResult.successData(file.length());
        }
        return BaseResult.successData(-1);
    }

    @ApiOperation(value = "上传图片", httpMethod = "POST")
    @PostMapping(value = "/uploadImg")
    public BaseResult uploadMovieImg(@Validated @RequestBody ImageForm imageForm,
                                     HttpServletRequest request) {
        File tmpFileDir = WebFileUtils.getTmpFileDir(request);
        File file = WebFileUtils.getTmpFileDir(request);
        if (Objects.isNull(file)) {
            return new BaseResult(ResultCode.IO_ERROR, "获取临时路径失败");
        }
        String filename = imageForm.getFileName();
        String imgData = imageForm.getImgData();
        String imgData2 = imageForm.getImgData2();
        if (!filename.contains(".")) {
            return new BaseResult(ResultCode.IO_ERROR, "文件格式不对");
        }
        filename = WebFileUtils.getFileNameWithTimestamp(filename);
        try {
            //写第一张图片
            writeBase64Image(new File(tmpFileDir, filename), imgData);
        } catch (IOException e) {
            logger.error("写图片失败"+filename, e);
            return new BaseResult(ResultCode.IO_ERROR, "上传文件失败");
        }
        //返回图片访问路径
        String url = request.getSession().getServletContext().getAttribute("_tmpPath") + filename;

        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("url",url);
        resultMap.put("fileName",filename);

        if (!StringUtils.isEmpty(imgData2)) {
            //写第二张图片
            filename = WebFileUtils.appendFilename(filename,"_detail");
            try {
                writeBase64Image(new File(tmpFileDir, filename), imgData2);
            } catch (IOException e) {
                logger.error("写第二张图片失败" + filename, e);
            }
        }
        return BaseResult.successData(resultMap);

    }

    private void writeBase64Image(File file, String imgData) throws IOException {
        String base64 = imgData.substring(imgData.indexOf(",") + 1);
        byte[] decoderBytes = Base64.getDecoder().decode(base64);
        FileUtils.writeByteArrayToFile(file, decoderBytes);
    }

    @PostMapping("/uploadFilePair")
    public BaseResult upload(HttpServletRequest request,
                             @RequestParam(value = "fileName", required = false) String fileName,
                             @RequestParam(value = "md5", required = false) String md5,
                             @RequestParam(value = "taskId", required = false) Long taskId,
                             HttpServletResponse response) throws Exception {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取传入文件
        multipartRequest.setCharacterEncoding("utf-8");
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
        File saveFile = new File(videoFileDir, fileName);
        boolean finish = this.saveFile(saveFile, file, request);
        //完成上传就记录文件的MD5
        if (finish) {
            if ((!StringUtils.isEmpty(md5)
                    || !(StringUtils.isEmpty(md5 = FileMd5Util.getFileMD5(saveFile))))) {

                FileInfo fileInfo = new FileInfo();
                fileInfo.setMd5(md5);
                fileInfo.setName(fileName);
                fileInfoService.add(fileInfo);
            }
            if (taskId != null) {
                taskService.updateStatus(taskId, TaskStatus.DONE);
            }
        }
        // 设置返回值
        return BaseResult.successData(fileName);
    }


    private boolean saveFile(File saveFile, MultipartFile file,
                             HttpServletRequest request) throws Exception {

        long lStartPos;
        long startPosition;
        long endPosition;
        long totalSize;
        String contentRange = request.getHeader("Content-Range");
        if (logger.isDebugEnabled()) {
            logger.debug("上传文件：{} - {}", saveFile.getName(), contentRange);
        }
        if (StringUtils.isEmpty(contentRange)) {
            FileUtils.copyInputStreamToFile(file.getInputStream(), saveFile);
            return true;
        } else {
            // bytes 10000-19999/1157632     将获取到的数据进行处理截取出开始跟结束位置
            contentRange = contentRange.replace("bytes", "").trim();
            int splitIndex = contentRange.indexOf("/");
            String rangeStr = contentRange.substring(0, splitIndex);
            String totalStr = contentRange.substring(splitIndex + 1);
            String[] ranges = rangeStr.split("-");
            startPosition = Integer.parseInt(ranges[0]);
            endPosition = Integer.parseInt(ranges[1]);
            totalSize = Integer.parseInt(totalStr);
            //判断所上传文件是否已经存在，若存在则返回存在文件的大小
            RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rw");
            lStartPos = accessFile.length();

            //判断所上传文件片段是否存在，若存在则直接返回
            if (lStartPos > endPosition) {
                accessFile.close();
                return false;
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
                if (count != -1) {
                    accessFile.write(data, 0, count);
                }
            } finally {
                accessFile.close();
            }

            //
            return endPosition == totalSize - 1;

        }

    }


}
