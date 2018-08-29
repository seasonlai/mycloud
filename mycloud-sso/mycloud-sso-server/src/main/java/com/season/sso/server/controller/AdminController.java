package com.season.sso.server.controller;

import com.season.common.base.BaseResult;
import com.season.sso.server.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Administrator on 2018/7/17.
 */
@Api("管理员")
@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    UserService userService;

    @ApiOperation("管理员首页")
    @GetMapping({"/", "/index"})
    public ModelAndView index() {
        Subject subject = SecurityUtils.getSubject();
        ModelAndView mav = new ModelAndView("admin/profile");
        mav.addObject("user", subject.getPrincipal());
        return mav;
    }

    @ApiOperation("更改密码")
    @PostMapping("/password")
    public Object updatePwd(@RequestParam("oldPwd") String oldPwd, @RequestParam("newPwd") String newPwd) {
        userService.updatePassword(oldPwd, newPwd);
        return BaseResult.success();
    }

}
