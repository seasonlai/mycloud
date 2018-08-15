package com.season.sso.server.dto;

import java.io.Serializable;

/**
 * Created by season on 2018/7/24.
 */
public class TreeNode implements Serializable{

    private String name;
    private Integer pid;
    private Integer id;

    public TreeNode() {
    }

    public TreeNode(String name, Integer pid, Integer id) {
        this.name = name;
        this.pid = pid;
        this.id = id;
    }

    public static TreeNode rootNode(){
        return rootNode("根节点");
    }
    public static TreeNode rootNode(String name){
        return new TreeNode(name,null,0);
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
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
}
