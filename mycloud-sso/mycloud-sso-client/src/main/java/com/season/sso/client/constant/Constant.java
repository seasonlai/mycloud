package com.season.sso.client.constant;

import com.season.common.base.BaseConstant;

/**
 * Created by Administrator on 2018/6/2.
 */
public class Constant extends BaseConstant {

    /**
     * 未登录错误码
     */
    public static final int CODE_UNLOGIN = -444;
    /**
     * 密码错误
     */
    public static final int CODE_PASSWORD_ERROR = -446;
    /**
     * 账号锁定错误码
     */
    public static final int CODE_USER_LOCKED = -447;
    /**
     * 用户名已存在
     */
    public static final int CODE_USER_EXIST = -447;
    /**
     * session的key的前缀
     */
    public static final String SESSION_PREFIX = "sso_session";
    /**
     * 缓存key的前缀
     */
    public static final String CACHE_PREFIX = "sso_cache";
    /**
     * session过期时间，单位：秒
     */
    public static final int SESSION_EXIST_TIME=600;

    /**
     * 密码加密迭代次数
     */
    public static final int HASH_ITERATION = 1;
    /**
     * 密码加密算法
     */
    public static final String HASH_ALGORITHM = "md5";

    /**
     * 临时存储用户信息到session
     */
    public static final String CACHE_TMP_USER = "cache_tmp_user";
    public static final String SESSION_USER = "user";
}
