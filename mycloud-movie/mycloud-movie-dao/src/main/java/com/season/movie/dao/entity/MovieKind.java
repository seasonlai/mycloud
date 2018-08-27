package com.season.movie.dao.entity;

import javax.persistence.*;

@Table(name = "movie_kind")
public class MovieKind {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 电影id
     */
    @Column(name = "movie_id")
    private Long movieId;

    /**
     * 类型id
     */
    @Column(name = "kind_id")
    private Integer kindId;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取电影id
     *
     * @return movie_id - 电影id
     */
    public Long getMovieId() {
        return movieId;
    }

    /**
     * 设置电影id
     *
     * @param movieId 电影id
     */
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    /**
     * 获取类型id
     *
     * @return kind_id - 类型id
     */
    public Integer getKindId() {
        return kindId;
    }

    /**
     * 设置类型id
     *
     * @param kindId 类型id
     */
    public void setKindId(Integer kindId) {
        this.kindId = kindId;
    }
}