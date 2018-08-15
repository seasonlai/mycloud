package com.season.common.web.exception;

/**
 * Created by Administrator on 2018/7/6.
 */
public class CacheException extends RuntimeException {


    public static final String MESSAGE = "缓存异常";

    public CacheException() {
        super(MESSAGE);
    }

    public CacheException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
