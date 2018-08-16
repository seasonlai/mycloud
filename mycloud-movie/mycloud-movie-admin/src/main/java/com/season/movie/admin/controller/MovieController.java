package com.season.movie.admin.controller;

import com.season.common.base.BaseResult;
import com.season.movie.admin.form.MovieForm;
import org.apache.commons.io.FileUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/8/13.
 */
@RestController
@RequestMapping("/admin")
public class MovieController extends BaseController {


    @GetMapping("movieList")
    public ModelAndView movieList() {
        return new ModelAndView("movie/movieList");
    }

    @GetMapping("/movieUpload")
    public ModelAndView movieUpload() {
        return new ModelAndView("movie/movieUpload");
    }


//    public BaseResult addMovie(@Validated MovieForm movie){
//
//
//
//    }

    public BaseResult uploadMovieImg(MultipartFile multipartFile) {
        String url = null;
        try {
            //获取项目路径
            String realPath = request.getSession().getServletContext()
                    .getRealPath("");
            InputStream inputStream = multipartFile.getInputStream();
            String contextPath = request.getContextPath();
            //服务器根目录的路径
            String path = realPath.replace(contextPath.substring(1), "");
            //根目录下新建文件夹upload，存放上传图片
            String uploadPath = path + "upload";
            //获取文件名称
            String filename = getUploadFileName(multipartFile);
            //将文件上传的服务器根目录下的upload文件夹
            File file = new File(uploadPath, filename);
            FileUtils.copyInputStreamToFile(inputStream, file);
            //返回图片访问路径
            url = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + "/upload/" + filename;
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            new BaseResult()
        }
        return BaseResult.successData(url);

    }


    /**
     * 获取上传文件的名称,新文件名为原文件名加上时间戳
     *
     * @param multipartFile multipartFile
     * @return 文件名
     */
    private String getUploadFileName(MultipartFile multipartFile) {
        String uploadFileName = multipartFile.getOriginalFilename();
        String fileName = uploadFileName.substring(0,
                uploadFileName.lastIndexOf("."));
        String type = uploadFileName.substring(uploadFileName.lastIndexOf("."));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        String name = fileName + "_" + timeStr + type;
        return name;
    }


}
