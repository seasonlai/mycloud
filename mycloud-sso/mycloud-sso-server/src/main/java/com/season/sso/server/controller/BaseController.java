package com.season.sso.server.controller;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018/6/2.
 */
public class BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    /**
     * 统一异常处理
     *
     * @param request
     * @param response
     * @param exception
     */
    @ExceptionHandler
    public String exceptionHandler(HttpServletRequest request, HttpServletResponse response,
                                   Exception exception, Model model) {
        LOGGER.error("统一异常处理：", exception);

        if (HttpUtils.isAjaxRequest(request)) {
            request.setAttribute("requestHeader", "ajax");
        }

        BaseResult result = null;

        if (exception instanceof UnknownAccountException) {
            result = new BaseResult(BaseConstant.CODE_UNKNOW_ACCOUNT, "用户不存在");
        }
        // shiro没有权限异常
        else if (exception instanceof UnauthorizedException || exception instanceof AuthenticationException) {
            result = new BaseResult(BaseConstant.CODE_UNAUTHORIZED, "身份/权限验证失败");
        }
        // shiro会话已过期异常
        else if (exception instanceof InvalidSessionException) {
            result = new BaseResult(BaseConstant.CODE_INVALID_SESSION, "会话已过期");
        }
        //token过期
        else if (exception instanceof TokenExpiredException) {
            result = new BaseResult(BaseConstant.CODE_INVALID_TOKEN, "token已过期");
        }
        //自定义的异常
        else if (exception instanceof BaseException) {
            BaseException e = (BaseException) exception;
            result = new BaseResult(e.getCode(), e.getMessage());
        } else if (exception instanceof ServletRequestBindingException) {
            result = new BaseResult(ResultCode.VALIDATE_ERROR, "请求参数姿势不对");
        }
        //其他异常
        else {
            result = new BaseResult(-1, exception.getMessage());
        }

        if (HttpUtils.isAjaxRequest(request)) {
            HttpUtils.writeJson(result, response);
        } else {
//            model.addAttribute("ex",result.getMsg());
            return result.getMsg();
//            try {
//                request.setAttribute("ex", result.getMsg());
//                model.addAttribute("ex",result.getMsg());
//                response.sendRedirect("/sso/error");
//            } catch (IOException e) {
//                LOGGER.error("重定向出错：错误：{}，异常：{}", result.getMsg(), e);
//            }
        }
        return null;
    }


}
