package com.season.sso.client.util;

import com.season.common.web.util.SpringUtil;
import com.season.sso.client.model.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;

public class ShiroUtil {

    private static SessionDAO sessionDAo
            = SpringUtil.getBean(DefaultWebSessionManager.class)
            .getSessionDAO();

    private ShiroUtil() {
    }

    /**
     * 获取指定用户名的Session
     */
    private static Session getSessionByUsername(String username) {

        Assert.notNull(sessionDAo, "shiroUtil: redisSessionDAO为空");

        if (Objects.isNull(username)) {
            return null;
        }
        Collection<Session> sessions = sessionDAo.getActiveSessions();
        if (CollectionUtils.isEmpty(sessions) || StringUtils.isEmpty(username))
            return null;
        LoginUser user;
        for (Session session : sessions) {
            user = getLoginUser(session);
            if (user == null) {
                continue;
            }
            if (Objects.equals(user.getName(), username)) {
                return session;
            }
        }
        return null;
    }

    public static LoginUser getLoginUser(Session session) {
        if (Objects.isNull(session))
            return null;
        Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        if (attribute != null) {
            Object primaryPrincipal = ((SimplePrincipalCollection) attribute).getPrimaryPrincipal();
            if (primaryPrincipal != null && primaryPrincipal instanceof LoginUser)
                return (LoginUser) primaryPrincipal;
        }
        return null;
    }

    /**
     * 获取指定用户名的Session
     */
    private static Session getSessionByUserId(Long userId) {
        Assert.notNull(sessionDAo, "shiroUtil: redisSessionDAO为空");
        if (Objects.isNull(userId)) {
            return null;
        }

        Collection<Session> sessions = sessionDAo.getActiveSessions();
        if (CollectionUtils.isEmpty(sessions) || userId == null)
            return null;
        LoginUser user;
        for (Session session : sessions) {
            user = getLoginUser(session);
            if (user == null) {
                continue;
            }
            if (Objects.equals(user.getId(), userId)) {
                return session;
            }
        }
        return null;
    }

    /**
     * 删除用户缓存信息
     *
     * @param username        用户名
     * @param isRemoveSession 是否删除session，删除后用户需重新登录
     */
    public static void kickOutUser(String username, boolean isRemoveSession) {
        kickOutUser(getSessionByUsername(username), isRemoveSession);
    }

    public static void kickOutUser(Long userId, boolean isRemoveSession) {
        kickOutUser(getSessionByUserId(userId), isRemoveSession);
    }

    public static void kickOutAllUser(boolean isRemoveSession) {
        Assert.notNull(sessionDAo, "shiroUtil: redisSessionDAO为空");
        Collection<Session> activeSessions = sessionDAo.getActiveSessions();
        if (CollectionUtils.isEmpty(activeSessions)) {
            return;
        }
        for (Session session : activeSessions) {
            kickOutUser(session, isRemoveSession);
        }
    }

    private static void kickOutUser(Session session, boolean isRemoveSession) {

        Assert.notNull(sessionDAo, "shiroUtil: redisSessionDAO为空");

        if (session == null) {
            return;
        }
        //删除session
        if (isRemoveSession) {
            sessionDAo.delete(session);
        }
        Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        DefaultWebSecurityManager securityManager =
                SpringUtil.getBean(DefaultWebSecurityManager.class);
//                (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        Authenticator authc = securityManager.getAuthenticator();
        //删除cache，在访问受限接口时会重新授权
        ((LogoutAware) authc).onLogout((SimplePrincipalCollection) attribute);
    }

}