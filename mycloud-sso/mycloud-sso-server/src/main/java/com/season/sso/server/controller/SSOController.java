package com.season.sso.server.controller;

import com.season.common.base.BaseException;
import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.sso.client.model.LoginPermission;
import com.season.sso.client.model.LoginRole;
import com.season.sso.client.model.LoginUser;
import com.season.sso.client.constant.Constant;
import com.season.sso.server.entity.Permission;
import com.season.sso.server.entity.Role;
import com.season.sso.server.entity.User;
import com.season.sso.server.service.AppService;
import com.season.sso.server.service.PermissionService;
import com.season.sso.server.service.RoleService;
import com.season.sso.server.service.UserService;
import com.season.common.web.captcha.CaptchaHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/6/1.
 */
@RestController
@RequestMapping("/sso")
@Api("单点登录")
public class SSOController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(SSOController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AppService appService;

    @Value("${app.cache.redis.keyPrefix:mycloud:cache:}")
    private String keyPrefix;
    @Value("${sso.server.idPrefix:server-session-id:}")
    private String MYCLOUD_SERVER_SESSION_ID;
    @Value("${sso.server.idsPrefix:server-session-ids:}")
    private String MYCLOUD_SERVER_SESSION_IDS;
    @Value("${sso.server.code:server-code:}")
    private String MYCLOUD_SERVER_CODE;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String getKey(String key) {
        return keyPrefix + key;
    }

    @ApiIgnore
    @RequestMapping(value = "/unLogin", method = RequestMethod.GET)
    public BaseResult unLogin() {
        return new BaseResult(Constant.CODE_UNLOGIN, "未登录");
    }

    @ApiIgnore
    @RequestMapping(value = "/index")
    @RequiresAuthentication
    public BaseResult index() {
        return BaseResult.success("登录成功");
    }


    //登出
    @ApiOperation(value = "登出", httpMethod = "GET")
    @RequestMapping(value = "/logout")
    @RequiresAuthentication
    public BaseResult logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return BaseResult.success("已登出");
    }

    @ApiOperation(value = "登录页面", httpMethod = "GET")
    @GetMapping("/login")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        //是否是mycloud应用的重定位
        Object appCode = request.getParameter("appCode");
        if (Objects.isNull(appCode)) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, "非法访问");
        }
        if (!appService.isEnable(appCode.toString())) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, String.format("应用未注册:%s", appCode));
        }

        String backUrl = String.valueOf(request.getParameter("backUrl"));

        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Serializable sessionId = session.getId();
        String token = redisTemplate.opsForValue()
                .get(getKey(MYCLOUD_SERVER_SESSION_ID + sessionId));
        if (!StringUtils.isEmpty(token)) {//已经登录
            //返回backUrl
            if (StringUtils.isEmpty(backUrl)) {
                backUrl = "/sso/index";
            }
            LoginUser user = (LoginUser) subject.getPrincipal();
            if (backUrl.contains("?")) {
                backUrl += "&token=" + token + "&username=" + user.getName();
            } else {
                backUrl += "?token=" + token + "&username=" + user.getName();
            }
            logger.debug("认证中心帐号通过，带code回跳：{}", backUrl);
            return new ModelAndView("redirect:" + backUrl);
        }
        request.setAttribute("appCode", appCode);
        request.setAttribute("backUrl", backUrl);
        return new ModelAndView("login");
    }

    //post登录
    @ApiOperation(value = "登录", httpMethod = "POST", notes = "使用了shiro框架")
    @PostMapping(value = "/login")
    public BaseResult login(@RequestParam("username") String userName,
                            @RequestParam("password") String pwd,
                            @RequestParam("captcha") String captcha,
                            @RequestParam(value = "appCode", required = false) String appCode,
                            @RequestParam(value = "backUrl", required = false) String backUrl,
                            HttpServletRequest request
    ) {

        if (StringUtils.isEmpty(appCode)) {
            throw new BaseException(ResultCode.ERROR, "非法访问");
        }

        if (!appService.isEnable(appCode)) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, "应用未注册");
        }

        if (!CaptchaHelper.validate(request, captcha)) {
            return new BaseResult(ResultCode.CAPTCHA_ERROR, "验证码出错");
        }


        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Serializable sessionId = session.getId();
        String token = redisTemplate.opsForValue()
                .get(getKey(MYCLOUD_SERVER_SESSION_ID + sessionId));
        if (StringUtils.isEmpty(token)) {
            //添加用户认证信息
            UsernamePasswordToken usernamePasswordToken
                    = new UsernamePasswordToken(userName, pwd);
            //进行验证，捕获异常，然后返回对应信息
            try {
                subject.login(usernamePasswordToken);
            } catch (UnknownAccountException e) {
                return new BaseResult(Constant.CODE_UNKNOW_ACCOUNT, "帐号不存在！");
            } catch (IncorrectCredentialsException e) {
                return new BaseResult(Constant.CODE_PASSWORD_ERROR, "帐号或密码错误！");
            } catch (LockedAccountException e) {
                return new BaseResult(Constant.CODE_USER_LOCKED, "帐号已锁定！");
            }
            LoginUser user = (LoginUser) subject.getPrincipal();
            userService.loginSuccess(user, request);
            logger.info("登陆成功 - {}", subject.getPrincipal().toString());

            token = UUID.randomUUID().toString();
            redisTemplate.opsForValue()
                    .set(getKey(MYCLOUD_SERVER_SESSION_ID + sessionId), token);
//            redisTemplate.opsForList()
//                    .leftPush(getKey(MYCLOUD_SERVER_SESSION_IDS + token), appCode);
//            redisTemplate.expire(getKey(MYCLOUD_SERVER_SESSION_IDS + token),
//                    session.getTimeout(), TimeUnit.MILLISECONDS);
            redisTemplate.opsForValue().set(getKey(MYCLOUD_SERVER_CODE + token), token);
            if (StringUtils.isEmpty(backUrl)) {
                backUrl = "/sso/index";
            }
            if (backUrl.contains("?")) {
                backUrl += "&token=" + token + "&username=" + user.getName();
            } else {
                backUrl += "?token=" + token + "&username=" + user.getName();
            }
        }
        return BaseResult.success("登录成功", backUrl);//, JWTUtils.sign(userName, pwd)
    }


    //错误页面展示
    @ApiIgnore
    @GetMapping(value = "/error")
    public ModelAndView error() {
        return new ModelAndView("error/error");
    }

}
