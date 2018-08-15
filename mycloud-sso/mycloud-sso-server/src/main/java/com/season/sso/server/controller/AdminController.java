package com.season.sso.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Administrator on 2018/7/17.
 */
@Api("管理员")
@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController{

    @ApiOperation("管理员首页")
    @GetMapping({"/","/index"})
    public ModelAndView index(){
        Subject subject = SecurityUtils.getSubject();
        ModelAndView mav = new ModelAndView("admin/profile");
        mav.addObject("user",subject.getPrincipal());
        return mav;
    }

}
