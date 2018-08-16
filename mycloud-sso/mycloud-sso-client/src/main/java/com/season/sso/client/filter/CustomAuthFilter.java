package com.season.sso.client.filter;

import com.season.common.base.BaseException;
import com.season.common.base.BaseResult;
import com.season.common.web.util.HttpUtils;
import com.season.sso.client.constant.Constant;
import com.season.sso.client.model.LoginUser;
import com.season.sso.client.service.SSOService;
import com.season.sso.client.util.ShiroUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/7/27.
 */
public class CustomAuthFilter extends FormAuthenticationFilter {

//    private final String needToSaveRequest = "need_save_requeset";


    static Logger logger = LoggerFactory.getLogger(CustomAuthFilter.class);
    private String appCode;
    @Value("${mycloud.sso.type:client}")
    private String ssoType;

    @Autowired
    private Constant constant;

    @Autowired
    private SSOService SSOService;

    @Autowired
    StringRedisTemplate redisTemplate;

    public CustomAuthFilter() {
    }

    public CustomAuthFilter(String appCode) {
        this.appCode = appCode;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        logger.debug("应用码: {}  sessionId:{}", appCode, subject.getSession().getId());
        // 判断请求类型

        if ("client".equals(ssoType)) {
            return validateClient(request, response);
        }
        if ("server".equals(ssoType)) {
            return subject.isAuthenticated();
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (this.isLoginRequest(request, response)) {
            return true;
        } else {
            Map<String, String> query = new HashMap<>(4);
            query.put("appCode", appCode);
            if (!HttpUtils.isAjaxRequest((HttpServletRequest) request)) {
                query.put("backUrl", ((HttpServletRequest) request).getRequestURL().toString());
            }
            WebUtils.issueRedirect(request, response, getLoginUrl(), query);
            return false;
        }
    }


    /**
     * 认证中心登录成功带回code
     *
     * @param request
     */
    private boolean validateClient(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);
        Session session = subject.getSession();
        String sessionId = session.getId().toString();
        // 判断局部会话是否登录
        String token = redisTemplate.opsForValue()
                .get(getKey(constant.getMYCLOUD_CLIENT_SESSION_ID() + session.getId()));
        if (StringUtils.isNotBlank(token)) {
            // 移除url中的code参数
            if (null != request.getParameter("token")) {
                String backUrl = HttpUtils.getUrlWithoutParams(WebUtils.toHttp(request),
                        "username", "token");
                try {
                    WebUtils.issueRedirect(request, response, backUrl);
                } catch (IOException e) {
                    logger.error("局部会话已登录，移除code参数跳转出错：", e);
                }
            } else {
                return true;
            }
        }
        int timeOut = (int) session.getTimeout() / 1000;
        // 判断是否有认证中心code
        token = request.getParameter("token");
        // 已拿到token
        if (StringUtils.isNotBlank(token)) {
            // Post去校验token
            BaseResult loginResult = SSOService.validateAppLogin(request, token);
            if (loginResult.getCode() == 0 && token.equals(loginResult.getData())) {

                // code校验正确，创建局部会话
                redisTemplate.opsForValue()
                        .set(getKey(constant.getMYCLOUD_CLIENT_SESSION_ID() + sessionId), token, timeOut, TimeUnit.SECONDS);
                // 保存code对应的局部会话sessionId，方便退出操作
//                redisTemplate.opsForList()
//                        .rightPush(getKey(MYCLOUD_CLIENT_SESSION_IDS + token), sessionId);
//                redisTemplate.expire(getKey(MYCLOUD_CLIENT_SESSION_IDS + token), timeOut, TimeUnit.SECONDS);
//                logger.debug("当前token={}，对应的注册系统个数：{}个", token,
//                        redisTemplate.opsForList().size(getKey(MYCLOUD_CLIENT_SESSION_IDS + token)));
                // 移除url中的token参数
                String backUrl = HttpUtils.getUrlWithoutParams(WebUtils.toHttp(request),
                        "username", "token");
                // 返回请求资源
                try {
                    // client无密认证
                    String username = request.getParameter("username");
                    subject.login(new UsernamePasswordToken(username, ""));
                    WebUtils.issueRedirect(request, response, backUrl);
                    return true;
                } catch (IOException e) {
                    logger.error("已拿到code，移除token参数跳转出错：", e);
                }

            } else {
                logger.warn(loginResult.getMsg());
            }
        }
        return false;
    }

    private String getKey(String key) {
        return constant.getKeyPrefix() + key;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
}
