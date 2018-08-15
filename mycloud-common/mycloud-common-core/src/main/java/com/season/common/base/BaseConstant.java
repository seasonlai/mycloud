package com.season.common.base;

/**
 * Created by Administrator on 2018/6/1.
 */
public class BaseConstant {

    /**
     * 用户不存在
     */
    public static final int CODE_UNKNOW_ACCOUNT = -555;

    /**
     * 认证失败错误码
     */
    public static final int CODE_UNAUTHORIZED = -666;
    /**
     * 会话过期错误码
     */
    public static final int CODE_INVALID_SESSION = -777;
    /**
     * token过期错误码
     */
    public static final int CODE_INVALID_TOKEN = -888;

    /**
     * TOKEN有效时间  单位：毫秒
     */
    public static final int TOKEN_EXPIRE_TIME = 5*60*1000;

}
