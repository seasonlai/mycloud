package com.season.movie.admin.controller;

import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.web.util.WebFileUtils;
import com.season.movie.admin.form.MovieForm;
import com.season.movie.dao.entity.Kind;
import com.season.movie.service.service.KindServcie;
import com.season.movie.service.service.MovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/13.
 */
@Api("管理员影片管理模块")
@RestController
@RequestMapping("/admin")
public class MovieController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    KindServcie kindServcie;
    @Autowired
    MovieService movieService;

    @ApiOperation(value = "影片列表页面", httpMethod = "GET")
    @GetMapping("/movieList")
    public ModelAndView movieList() {
        return new ModelAndView("movie/movieList");
    }

    @ApiOperation(value = "影片上传页面", httpMethod = "GET")
    @GetMapping("/movieUpload")
    public ModelAndView movieUpload() {
        ModelAndView mav = new ModelAndView("movie/movieUpload");
        List<Kind> kinds = kindServcie.listAll();
        mav.addObject("kinds",kinds);
        return mav;
    }

    @ApiOperation("新增电影")
    @PostMapping("/addMovie")
    public BaseResult addMovie(@Validated MovieForm movieForm, HttpServletRequest request){

        File tmpFileDir = WebFileUtils.getTmpFileDir(request);
        File goalFileDir = WebFileUtils.getImgFileDir(request);
        movieService.addMovie(movieForm.movie(),
                movieForm.movieDetail(),
                tmpFileDir,goalFileDir,
                String.valueOf(request.getServletContext()
                        .getAttribute("_imgPath")));

        return BaseResult.success();

    }


//    @ApiOperation(value = "上传影片封面", httpMethod = "POST")
//    @PostMapping("/uploadImg")
//    public BaseResult uploadMovieImg(@RequestParam("uploadImg") MultipartFile multipartFile,
//                                     HttpServletRequest request) {
//        String url = null;
//        try {
//            File file = getTmpFileDir(request);
//            if (Objects.isNull(file)) {
//                return new BaseResult(ResultCode.IO_ERROR, "获取临时路径失败");
//            }
//            //获取文件名称
//            String filename = getFileNameWithTimestamp(multipartFile);
//            //将文件上传的服务器根目录下的upload文件夹
//            file = new File(file, filename);
////            Thumbnails.of(multipartFile.getInputStream()).
//            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
//            //返回图片访问路径
//            url = request.getSession().getServletContext().getAttribute("_tmpPath") + filename;
//        } catch (IOException e) {
//            logger.error("写文件失败", e);
//            return new BaseResult(ResultCode.IO_ERROR, "上传文件失败");
//        }
//        return BaseResult.successData(url);

    //    }
    @ApiOperation(value = "上传影片封面", httpMethod = "POST")
    @PostMapping("/uploadImg")
    public BaseResult uploadMovieImg(@RequestParam("imgData") String imgData,
                                     @RequestParam("filename") String filename,
                                     HttpServletRequest request) {
        String url = null;
        File tmpFileDir =  WebFileUtils.getTmpFileDir(request);
        File file =  WebFileUtils.getTmpFileDir(request);
        if (Objects.isNull(file)) {
            return new BaseResult(ResultCode.IO_ERROR, "获取临时路径失败");
        }
        filename =  WebFileUtils.getFileNameWithTimestamp(filename);
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


}
