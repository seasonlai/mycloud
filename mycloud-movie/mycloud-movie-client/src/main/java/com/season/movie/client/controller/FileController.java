package com.season.movie.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Administrator on 2018/8/1.
 */
@Controller
@RequestMapping("/static")
public class FileController {



    @GetMapping("/image")
    public void images(@RequestParam("img") String imgUrl) {

    }

}
