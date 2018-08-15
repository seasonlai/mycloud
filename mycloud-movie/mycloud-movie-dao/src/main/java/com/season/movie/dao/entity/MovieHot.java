package com.season.movie.dao.entity;

import javax.persistence.*;

@Table(name = "movie_hot")
public class MovieHot {
    /**
     * 电影id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 获取电影id
     *
     * @return id - 电影id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置电影id
     *
     * @param id 电影id
     */
    public void setId(Long id) {
        this.id = id;
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
}