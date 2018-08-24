package com.season.movie.client.controller;

import com.season.common.base.BaseResult;
import com.season.movie.dao.entity.Movie;
import com.season.movie.service.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Administrator on 2018/7/31.
 */
@RestController
@RequestMapping("/")
public class HomeController extends BaseController {

    @Autowired
    MovieService movieService;

    @GetMapping
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("index");
        List<Movie> movies_hot = movieService.listHot(0, 12);
        List<Movie> movies_new =movieService.listNew(0,5);
        List<Movie> movies_classic =movieService.listClassic(0,5);
        mav.addObject("hotList", movies_hot);
        mav.addObject("newList", movies_new);
        mav.addObject("classicList", movies_classic);
        return mav;
    }

    @PostMapping()
    public BaseResult listHot() {
        return null;
    }

}
