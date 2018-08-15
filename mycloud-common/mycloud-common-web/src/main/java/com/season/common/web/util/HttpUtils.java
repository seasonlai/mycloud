package com.season.common.web.util;

import com.season.common.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/1.
 */
public class HttpUtils {


    /**
     * 移除url中的code、username参数
     *
     * @param request
     * @return
     */
    public static String getUrlWithoutParams(HttpServletRequest request, String... outParams) {
        StringBuffer result = request.getRequestURL();
        String params = "";
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            boolean isAdd = true;
            if (outParams != null && outParams.length > 0) {
                for (String outParam : outParams) {
                    if (outParam.equals(key))
                        isAdd = false;
                }
            }
            if (isAdd) {
                if ("".equals(params)) {
                    params = entry.getKey() + "=" + entry.getValue()[0];
                } else {
                    params += "&" + entry.getKey() + "=" + entry.getValue()[0];
                }
            }
        }
        if (!StringUtils.isBlank(params)) {
            result = result.append("?").append(params);
        }
        return result.toString();
    }


    /**
     * 返回：/项目名/url
     *
     * @param request
     * @param url
     * @return /项目名/url
     */
    protected static String getAppbaseUrl(HttpServletRequest request, String url) {
        Assert.hasLength(url, "url不能为空");
        Assert.isTrue(url.startsWith("/"), "url必须以/开头");
        return request.getContextPath() + url;
    }

    /**
     * 返回：schema://ip:port/项目名/url
     *
     * @param request
     * @param url
     * @return
     */
    protected static String getWholeUrl(HttpServletRequest request, String url) {
        Assert.hasLength(url, "url不能为空");
        Assert.isTrue(url.startsWith("/"), "url必须以/开头");
        StringBuffer sb = request.getRequestURL();
        int i = sb.indexOf(request.getContextPath());
        sb.delete(i + request.getContextPath().length(), sb.length());
        return sb.append(url).toString();
    }


    /**
     * 是否是Ajax请求
     *
     * @param request
     * @return
     * @author SHANHY
     * @create 2017年4月4日
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("x-requested-with");
        if (requestedWith != null && requestedWith.equalsIgnoreCase("XMLHttpRequest")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 输出JSON
     *
     * @param response
     * @author SHANHY
     * @create 2017年4月4日
     */
    public static void writeJson(Object data, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            out = response.getWriter();
            out.write(JsonUtils.parseBeanToJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
