package com.season.common.base;

public class BaseResult{

    public int code;

    public String msg;

    /**
     * 数据结果集
     */
    public Object data;

    public BaseResult() {
    }

    public BaseResult(int code, String msg) {
        this(code, msg, null);
    }

    public BaseResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {

        this.data = data;
    }

    public static BaseResult success(String msg, Object data){
        return new BaseResult(0,msg,data);
    }

    public static BaseResult success(String msg){

        return new BaseResult(0,msg);
    }

    public static BaseResult successData(Object data){
        return new BaseResult(0,"成功",data);
    }

    public static BaseResult success(){
        return new BaseResult(0,"成功",null);
    }
}