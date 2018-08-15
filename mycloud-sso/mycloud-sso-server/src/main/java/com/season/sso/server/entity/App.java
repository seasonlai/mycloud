package com.season.sso.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2018/7/17.
 */
@Entity
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appId;
    private String appName;
    private String appCode;
    private Timestamp createTime;
    private byte enable;
    private Integer sort;

    @JsonIgnore
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "app_id",foreignKey = @ForeignKey(name = "role_app_id"))
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "app_id" ,foreignKey = @ForeignKey(name = "permission_app_id"))
    private Set<Permission> permissions = new HashSet<>();

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Integer getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public byte getEnable() {
        return enable;
    }

    public void setEnable(byte enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        App app = (App) o;

        if (appId != app.appId) return false;
        if (enable != app.enable) return false;
        if (appName != null ? !appName.equals(app.appName) : app.appName != null) return false;
        if (appCode != null ? !appCode.equals(app.appCode) : app.appCode != null) return false;
        if (createTime != null ? !createTime.equals(app.createTime) : app.createTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = appId;
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (appCode != null ? appCode.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (int) enable;
        return result;
    }
}
