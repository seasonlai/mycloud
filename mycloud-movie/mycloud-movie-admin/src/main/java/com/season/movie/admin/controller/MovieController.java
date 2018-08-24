package com.season.movie.admin.controller;

import com.github.pagehelper.Page;
import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.web.util.WebFileUtils;
import com.season.movie.admin.form.MovieForm;
import com.season.movie.dao.entity.Kind;
import com.season.movie.dao.entity.Movie;
import com.season.movie.dao.entity.Quality;
import com.season.movie.dao.entity.Task;
import com.season.movie.service.service.KindServcie;
import com.season.movie.service.service.MovieService;
import com.season.movie.service.service.QualityService;
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
    @Autowired
    QualityService qualityService;

    @ApiOperation(value = "影片列表页面", httpMethod = "GET")
    @GetMapping("/movieList")
    public ModelAndView movieList() {
        ModelAndView modelAndView = new ModelAndView("movie/movieList");
        List<Kind> kinds = kindServcie.listAll();
        modelAndView.addObject("kinds",kinds);
        return modelAndView;
    }

    @ApiOperation(value = "影片添加页面", httpMethod = "GET")
    @GetMapping("/movieAdd")
    public ModelAndView movieAdd() {
        ModelAndView mav = new ModelAndView("movie/movieAdd");
        List<Kind> kinds = kindServcie.listAll();
        mav.addObject("kinds", kinds);
        return mav;
    }
    @ApiOperation(value = "影片上传页面", httpMethod = "GET")
    @GetMapping("/movieUpload")
    public ModelAndView movieUpload() {
        ModelAndView mav = new ModelAndView("movie/movieUpload");
        List<Quality> qualities = qualityService.listAll();
        mav.addObject("qualities", qualities);
        return mav;
    }

    @ApiOperation("新增电影")
    @PostMapping("/addMovie")
    public BaseResult addMovie(@Validated MovieForm movieForm, HttpServletRequest request) {

        File tmpFileDir = WebFileUtils.getTmpFileDir(request);
        File goalFileDir = WebFileUtils.getImgFileDir(request);
        movieService.addMovie(movieForm.movie(),
                movieForm.movieDetail(), tmpFileDir, goalFileDir);
        return BaseResult.success();

    }

    @ApiOperation("分页查询电影")
    @PostMapping("/movieList")
    public BaseResult movieListPage(Movie movie
            , @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
            , @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        return BaseResult.successData(movieService.listAll(movie, pageNum, pageSize));
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


}
