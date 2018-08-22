package com.season.sso.client.util;

import com.season.sso.client.model.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Created by Administrator on 2018/8/22.
 */
public class SSOClientUtil {

    public static LoginUser getLoginUser() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return null;
        }
        Object principal = subject.getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }
        return null;
    }

}
