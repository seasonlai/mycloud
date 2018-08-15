package com.season.sso.client.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/18.
 */
public class LoginUser implements Serializable {

    private Long id;
    private String name;
    private String lastIp;
    private Date createTime;
    private Date lastTime;

    public LoginUser() {
    }

    public LoginUser(Long id, String name, String lastIp, Date createTime, Date lastTime) {
        this.id = id;
        this.name = name;
        this.lastIp = lastIp;
        this.createTime = createTime;
        this.lastTime = lastTime;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return "LoginUser{" +
                "name='" + name + '\'' +
                ", lastIp='" + lastIp + '\'' +
                ", createTime=" + createTime +
                ", lastTime=" + lastTime +
                '}';
    }

}
