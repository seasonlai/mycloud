package com.season.sso.server.dto;

/**
 * Created by Administrator on 2018/7/28.
 */
public class UserRole {


    private Integer id;
    private String name;
    private boolean author;

    public UserRole(Integer id, String name, boolean author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAuthor() {
        return author;
    }

    public void setAuthor(boolean author) {
        this.author = author;
    }
}
