package com.season.movie.dao.entity;

import javax.persistence.*;

public class FileInfo {
    /**
     * 文件ID
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 文件名
     */
    private String name;

    /**
     * MD5的值
     */
    private String md5;
    /**
     * 引用指向计数
     */
    private Integer pointer;

    /**
     * 获取文件ID
     *
     * @return id - 文件ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置文件ID
     *
     * @param id 文件ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取文件名
     *
     * @return name - 文件名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置文件名
     *
     * @param name 文件名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取MD5的值
     *
     * @return md5 - MD5的值
     */
    public String getMd5() {
        return md5;
    }

    /**
     * 设置MD5的值
     *
     * @param md5 MD5的值
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Integer getPointer() {
        return pointer;
    }

    public void setPointer(Integer pointer) {
        this.pointer = pointer;
    }
}