package com.season.movie.dao.entity;

import javax.persistence.*;

public class Kind {
    /**
     * 类型id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 类型名
     */
    private String name;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 获取类型id
     *
     * @return id - 类型id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置类型id
     *
     * @param id 类型id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取类型名
     *
     * @return name - 类型名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置类型名
     *
     * @param name 类型名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取排序号
     *
     * @return sort - 排序号
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序号
     *
     * @param sort 排序号
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Kind kind = (Kind) o;

        if (id != null ? !id.equals(kind.id) : kind.id != null) return false;
        if (name != null ? !name.equals(kind.name) : kind.name != null) return false;
        return sort != null ? sort.equals(kind.sort) : kind.sort == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        return result;
    }
}