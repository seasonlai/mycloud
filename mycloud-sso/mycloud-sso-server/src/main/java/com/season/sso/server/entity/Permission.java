package com.season.sso.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Administrator on 2018/7/17.
 */
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer permissionId;
    private String permissionName;
    private byte menu;
    private byte enable;
    private String description;
    private String permissionUrl;
    private String permissionNeed;
    private Integer parentId;
    private Date createTime;
    private Integer sort;
    @Column(name = "permission_app_id")
    private Integer permissionAppId;


    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getPermissionAppId() {
        return permissionAppId;
    }

    public void setPermissionAppId(Integer appId) {
        this.permissionAppId = appId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getPermissionUrl() {
        return permissionUrl;
    }

    public void setPermissionUrl(String url) {
        this.permissionUrl = url;
    }

    public String getPermissionNeed() {
        return permissionNeed;
    }

    public void setPermissionNeed(String permissionNeed) {
        this.permissionNeed = permissionNeed;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

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

    public byte getMenu() {
        return menu;
    }

    public void setMenu(byte menu) {
        this.menu = menu;
    }

    public byte getEnable() {
        return enable;
    }

    public void setEnable(byte enable) {
        this.enable = enable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission permission = (Permission) o;

        if (permissionId != permission.permissionId) return false;
        if (menu != permission.menu) return false;
        if (enable != permission.enable) return false;
        if (permissionName != null ? !permissionName.equals(permission.permissionName) : permission.permissionName != null)
            return false;
        if (description != null ? !description.equals(permission.description) : permission.description != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = permissionId;
        result = 31 * result + (permissionName != null ? permissionName.hashCode() : 0);
        result = 31 * result + (int) menu;
        result = 31 * result + (int) enable;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
