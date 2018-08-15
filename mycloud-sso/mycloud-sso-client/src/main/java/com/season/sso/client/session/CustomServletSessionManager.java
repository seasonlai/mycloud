package com.season.sso.client.session;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/8/13.
 */
public class CustomServletSessionManager extends ServletContainerSessionManager {

    private long sessionTimeout = 1800*1000;

    @Override
    protected Session createSession(HttpSession httpSession, String host) {
        Session session = super.createSession(httpSession, host);
        session.setTimeout(sessionTimeout);
        return session;
    }


    public void setSessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }
}
