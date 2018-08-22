package com.season.movie.dao.enums;

import com.season.movie.dao.base.BaseEnum;

/**
 * Created by season on 2018/8/19.
 */
public enum TaskStatus implements BaseEnum<TaskStatus>{

    CANCEL(-1,"已取消"),
    UNFINISH(1,"未完成"),
    DONE(2,"已完成"),
    TRASH(3,"放进垃圾箱"),
    DEL(4,"彻底删除")
    ;

    private int status;

    private String description;

    TaskStatus(int status, String value) {
        this.status = status;
        this.description = value;
    }

    @Override
    public Integer getCode() {
        return status;
    }

    @Override
    public String getValue() {
        return description;
    }
}
