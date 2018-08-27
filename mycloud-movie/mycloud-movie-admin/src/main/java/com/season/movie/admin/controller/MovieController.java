package com.season.movie.admin.controller;

import com.github.pagehelper.Page;
import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.web.util.WebFileUtils;
import com.season.movie.admin.form.MovieForm;
import com.season.movie.dao.entity.*;
import com.season.movie.service.service.*;
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
import java.util.Map;
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
    @Autowired
    DetailService detailService;
    @Autowired
    VideoService videoService;

    @ApiOperation(value = "影片列表页面", httpMethod = "GET")
    @GetMapping("/movieList")
    public ModelAndView movieList() {
        ModelAndView modelAndView = new ModelAndView("movie/movieList");
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

    @ApiOperation("影片详情编辑页")
    @GetMapping("/movieDetail/{movieId}")
    public ModelAndView movieDetail(@PathVariable("movieId") Long movieId) {
        ModelAndView mav = new ModelAndView("movie/movieDetail");
        //查询详情
        Map detail = detailService.getDetail(movieId);
        //查询类型
        List<Kind> kinds = kindServcie.listAll();
        Object info = detail.get("info");
        if (info instanceof Movie) {
            kinds.removeAll(((Movie) info).getKinds());//剩下的未选中的kinds
        }
        //查询关联的视频
        if (info instanceof Movie) {
            Video video = videoService.findById(((Movie) info).getVideoId());
            mav.addObject("video", video);
        }
        mav.addObject("kinds", kinds);
        mav.addObject("movie", detail);
        return mav;
    }

    @ApiOperation("新增电影")
    @PostMapping("/addMovie")
    public BaseResult addMovie(@Validated MovieForm movieForm, HttpServletRequest request) {

        File tmpFileDir = WebFileUtils.getTmpFileDir(request);
        File goalFileDir = WebFileUtils.getImgFileDir(request);
        movieService.addMovie(movieForm.movie(),
                movieForm.movieDetail(), movieForm.getMovieKind(), tmpFileDir, goalFileDir);
        return BaseResult.success();

    }

    @ApiOperation("更新电影封面")
    @PostMapping("/updateCover")
    public Object updateCover(Movie movie, HttpServletRequest request) {
        String fileName = movieService.updateCover(movie,
                WebFileUtils.getTmpFileDir(request),
                WebFileUtils.getImgFileDir(request));
        return BaseResult.successData(fileName);
    }

    @ApiOperation("更新电影封面")
    @PostMapping("/updateMovie")
    public Object updateMovie(@Validated MovieForm movieForm) {
        movieService.updateMovie(movieForm.movie(),
                movieForm.movieDetail(), movieForm.getMovieKind());
        return BaseResult.success();
    }

    @ApiOperation("更新电影的关联视频")
    @PostMapping("/updateMV")
    public Object updateMovieVideo(@RequestParam("movieId") Long movieId
            , @RequestParam("videoId") Long videoId) {
        movieService.updateMovieVideo(movieId, videoId);
        return BaseResult.success();
    }

    @ApiOperation("分页查询电影")
    @PostMapping("/movieList")
    public BaseResult movieListPage(Movie movie
            , @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
            , @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        return BaseResult.successData(movieService.listAll(movie, pageNum, pageSize));
    }

}
