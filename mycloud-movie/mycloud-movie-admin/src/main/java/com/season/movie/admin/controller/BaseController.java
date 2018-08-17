package com.season.movie.admin.controller;

import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.web.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/16.
 */
public class BaseController {

    static Logger logger = LoggerFactory.getLogger(BaseController.class);

    @ExceptionHandler
    public Object exception(Exception e, HttpServletRequest request, HttpServletResponse response) {

        if (HttpUtils.isAjaxRequest(request)) {
            HttpUtils.writeJson(new BaseResult(ResultCode.ERROR, e.getMessage()), response);
            return null;
        }
        return e.getMessage();
    }



}
