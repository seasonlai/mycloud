package com.season.movie.client.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.season.common.base.BaseConstant;
import com.season.common.base.BaseException;
import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.web.util.HttpUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2018/7/31.
 */
public class BaseController{


    private final static Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    /**
     * 统一异常处理
     *
     * @param request
     * @param response
     * @param exception
     */
    @ExceptionHandler
    public String exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        LOGGER.error("统一异常处理：", exception);

        if (HttpUtils.isAjaxRequest(request)) {
            request.setAttribute("requestHeader", "ajax");
        }

        String errorStr;
        Integer errorCode;

        if (exception instanceof UnknownAccountException) {
            errorStr = "用户不存在";
            errorCode = BaseConstant.CODE_UNKNOW_ACCOUNT;
        }
        // shiro没有权限异常
        else if (exception instanceof UnauthorizedException
                || exception instanceof AuthenticationException) {
            errorStr = "身份/权限验证失败";
            errorCode = BaseConstant.CODE_UNAUTHORIZED;
        }
        // shiro会话已过期异常
        else if (exception instanceof InvalidSessionException) {
            errorStr = "会话已过期";
            errorCode = BaseConstant.CODE_INVALID_SESSION;
        }
        //token过期
        else if (exception instanceof TokenExpiredException) {
            errorStr = "token已过期";
            errorCode = BaseConstant.CODE_INVALID_TOKEN;
        }
        //自定义的异常
        else if (exception instanceof BaseException) {
            BaseException e = (BaseException) exception;
            errorStr = e.getMessage();
            errorCode = e.getCode();
        } else if (exception instanceof ServletRequestBindingException) {
            errorStr = "请求参数姿势不对";
            errorCode = ResultCode.VALIDATE_ERROR;
        }
        //其他异常
        else {
            errorStr = "未知错误，请联系管理员";
            errorCode = -1;
        }
        if (HttpUtils.isAjaxRequest(request))
            HttpUtils.writeJson(new BaseResult(errorCode, errorStr), response);
        else {
            request.setAttribute("ex", errorStr);
            try {
                WebUtils.issueRedirect(request, response, "/error/error.html");
            } catch (IOException e) {
                LOGGER.error("重定向出错：{},异常：{}", errorStr, e);
            }
        }
        return null;
    }


}
