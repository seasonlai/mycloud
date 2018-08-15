package com.season.sso.client.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Administrator on 2018/8/10.
 */
public class LoginRole implements Serializable {


    private Integer roleId;
    private String roleName;

    private Set<LoginPermission> permissions;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<LoginPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<LoginPermission> permissions) {
        this.permissions = permissions;
    }
}

