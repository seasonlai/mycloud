package com.season.sso.client.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/10.
 */
public class LoginPermission implements Serializable {

    private Integer permissionId;
    private String permissionName;

    private String permissionNeed;

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionNeed() {
        return permissionNeed;
    }

    public void setPermissionNeed(String permissionNeed) {
        this.permissionNeed = permissionNeed;
    }
}
