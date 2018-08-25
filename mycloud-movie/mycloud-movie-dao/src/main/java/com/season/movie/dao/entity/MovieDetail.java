package com.season.movie.dao.entity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "movie_detail")
public class MovieDetail {
    /**
     * 详细内容id
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
     * 导演
     */
    private String director;

    /**
     * 电影主演
     */
    private String actors;


    /**
     * 电影关键字
     */
    private String keyword;

    /**
     * 电影描述
     */
    private String description;


    /**
     * 获取详细内容id
     *
     * @return id - 详细内容id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置详细内容id
     *
     * @param id 详细内容id
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
     * 获取导演
     *
     * @return director - 导演
     */
    public String getDirector() {
        return director;
    }

    /**
     * 设置导演
     *
     * @param director 导演
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * 获取电影主演
     *
     * @return actors - 电影主演
     */
    public String getActors() {
        return actors;
    }

    /**
     * 设置电影主演
     *
     * @param actors 电影主演
     */
    public void setActors(String actors) {
        this.actors = actors;
    }


    /**
     * 获取电影关键字
     *
     * @return keyword - 电影关键字
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * 设置电影关键字
     *
     * @param keyword 电影关键字
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * 获取电影描述
     *
     * @return description - 电影描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置电影描述
     *
     * @param description 电影描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

}