package com.season.sso.client.filter;

import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.web.util.HttpUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/31.
 */
public class PermsFilter extends PermissionsAuthorizationFilter {

    private String appCode;

    public PermsFilter() {

    }

    public PermsFilter(String appCode) {
        this.appCode = appCode;
    }


    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = this.getSubject(request, response);

        if (subject.getPrincipal() == null) {
            Map<String, String> query = new HashMap<>(4);
            query.put("appCode", appCode);
            if (!HttpUtils.isAjaxRequest((HttpServletRequest) request)) {
                query.put("backUrl", ((HttpServletRequest) request).getRequestURL().toString());
            }
            WebUtils.issueRedirect(request, response, getLoginUrl(), query);
        } else {

            if (HttpUtils.isAjaxRequest((HttpServletRequest) request)) {
                HttpUtils.writeJson(new BaseResult(ResultCode.ERROR, "无访问/操作权限"), (HttpServletResponse) response);
            } else {
                String unauthorizedUrl = this.getUnauthorizedUrl();
                if (StringUtils.hasText(unauthorizedUrl)) {
                    request.setAttribute("ex", "无权限");
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } else {
                    WebUtils.toHttp(response).sendError(401);
                }
            }

        }
        return false;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
}
