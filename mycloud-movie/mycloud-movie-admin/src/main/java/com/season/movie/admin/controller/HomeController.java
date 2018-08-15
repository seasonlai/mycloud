package com.season.movie.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Administrator on 2018/8/13.
 */
@RestController
@RequestMapping("/admin")
public class HomeController {


    @GetMapping
    public ModelAndView index(){
        return new ModelAndView("movieList");
    }


}
