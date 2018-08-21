package com.season.movie.dao.entity;

import javax.persistence.*;

public class Quality {
    /**
     * 质量表id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    /**
     * 名称
     */
    private String name;

    /**
     * 获取质量表id
     *
     * @return id - 质量表id
     */
    public Byte getId() {
        return id;
    }

    /**
     * 设置质量表id
     *
     * @param id 质量表id
     */
    public void setId(Byte id) {
        this.id = id;
    }

    /**
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }
}