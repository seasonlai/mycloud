package com.season.movie.dao.entity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "movie_comment")
public class MovieComment {
    /**
     * 评论id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 电影ID
     */
    @Column(name = "movie_id")
    private Long movieId;

    /**
     * 评论者id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 评论时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 获取评论id
     *
     * @return id - 评论id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置评论id
     *
     * @param id 评论id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取电影ID
     *
     * @return movie_id - 电影ID
     */
    public Long getMovieId() {
        return movieId;
    }

    /**
     * 设置电影ID
     *
     * @param movieId 电影ID
     */
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    /**
     * 获取评论者id
     *
     * @return user_id - 评论者id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置评论者id
     *
     * @param userId 评论者id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取评论时间
     *
     * @return create_time - 评论时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置评论时间
     *
     * @param createTime 评论时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取评论内容
     *
     * @return content - 评论内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置评论内容
     *
     * @param content 评论内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}