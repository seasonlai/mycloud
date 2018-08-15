package com.season.movie.client.controller;

import com.season.movie.client.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Administrator on 2018/8/2.
 */
@Controller
@RequestMapping("/detail")
public class DetailController {

    @Autowired
    DetailService detailService;

    @GetMapping
    public String detailPage(@RequestParam("id") Long id, Model model) {
        model.addAttribute("movie", detailService.getDetail(id));
        return "detail";
    }


}
