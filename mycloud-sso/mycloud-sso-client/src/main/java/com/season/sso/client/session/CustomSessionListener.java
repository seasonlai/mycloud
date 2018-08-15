package com.season.sso.client.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * Created by Administrator on 2018/8/14.
 */
public class CustomSessionListener implements SessionListener{


    //会话创建触发 已进入shiro的过滤连就触发这个方法
    @Override
    public void onStart(Session session) {

    }

    //退出
    @Override
    public void onStop(Session session) {

    }

    //过期
    @Override
    public void onExpiration(Session session) {

    }


}
