package com.season.movie.dao.enums;

import com.season.movie.dao.base.BaseEnum;

/**
 * Created by Administrator on 2018/8/21.
 */
public enum VideoStatus implements BaseEnum<VideoStatus> {
    NOT_EXIST(-1,"不存在"),
    FORBIDDEN(-2,"禁止/侵权"),
    ENABLE(0,"可用/正常"),
    ;

    private int code;
    private String desc;

    VideoStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return desc;
    }
}
