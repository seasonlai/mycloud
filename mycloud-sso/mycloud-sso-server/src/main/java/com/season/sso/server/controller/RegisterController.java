package com.season.sso.server.controller;

import com.season.common.base.BaseResult;
import com.season.sso.client.constant.Constant;
import com.season.sso.server.entity.User;
import com.season.sso.server.service.UserService;
import com.season.common.web.captcha.CaptchaHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2018/6/2.
 */
@RestController
@Api("注册")
@RequestMapping("/register")
public class RegisterController extends BaseController {


    @Autowired
    private UserService userService;

    @ApiOperation("注册页面")
    @GetMapping()
    public ModelAndView register() {

        return new ModelAndView("register");
    }

    @ApiOperation(value = "新注册用户", httpMethod = "POST")
    @PostMapping()
    public BaseResult register(@RequestParam("username") String name,
                               @RequestParam("password") String pwd,
                               @RequestParam("captcha") String captcha,
                               HttpServletRequest request) {
        if(!CaptchaHelper.validate(request, captcha)){
            return new BaseResult(-1,"验证码错误");
        }
        if (userService.exist(name)) {
            return new BaseResult(Constant.CODE_USER_EXIST, "用户名已存在");
        }
        User user = userService.register(name, pwd);
        return BaseResult.success("注册成功", user);
    }


    @ApiOperation(value = "判断用户名是否已存在   ", httpMethod = "POST")
    @PostMapping(value = "/judgeUser")
    public String existUser(String username){
        if (userService.exist(username)) {
            return "false";
        }
        return "true";
    }



}
