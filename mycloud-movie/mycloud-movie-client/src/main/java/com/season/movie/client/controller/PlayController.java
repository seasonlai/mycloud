package com.season.movie.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2018/8/2.
 */
@Controller
@RequestMapping("/play")
public class PlayController {


    @GetMapping("/{movieId}")
    public String playPage(@PathVariable("movieId") Integer movieId) {
        return "play";
    }

}
