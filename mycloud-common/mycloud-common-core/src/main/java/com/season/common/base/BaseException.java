package com.season.common.base;

/**
 * Created by Administrator on 2018/6/1.
 */
public class BaseException extends RuntimeException {

    protected int code;

    public BaseException(String message){
        super(message);
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(Throwable t){
        super(t);
    }

    public BaseException(String msg,Throwable t){
        super(msg,t);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
