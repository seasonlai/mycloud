package com.season.movie.dao.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

public class Movie {
    /**
     * 电影id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 电影名
     */
    private String name;

    /**
     * 电影价格
     */
    private BigDecimal price;

    /**
     * 电影主要类型
     */
    private Integer kind;

    /**
     * 电影的多种类型，以逗号隔开
     */
    private String kinds;

    /**
     * 电影封面图
     */
    private String cover;

    /**
     * 统计播放次数
     */
    @Column(name = "play_count")
    private Long playCount;

    /**
     * 电影上映年代
     */
    @Column(name = "show_year")
    private Date showYear;

    /**
     * 创建、上传时间
     */
    @Column(name = "create_time")
    private Date createTime;

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
     * 获取电影名
     *
     * @return name - 电影名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置电影名
     *
     * @param name 电影名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取电影价格
     *
     * @return price - 电影价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置电影价格
     *
     * @param price 电影价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取电影主要类型
     *
     * @return kind - 电影主要类型
     */
    public Integer getKind() {
        return kind;
    }

    /**
     * 设置电影主要类型
     *
     * @param kind 电影主要类型
     */
    public void setKind(Integer kind) {
        this.kind = kind;
    }

    /**
     * 获取电影的多种类型，以逗号隔开
     *
     * @return kinds - 电影的多种类型，以逗号隔开
     */
    public String getKinds() {
        return kinds;
    }

    /**
     * 设置电影的多种类型，以逗号隔开
     *
     * @param kinds 电影的多种类型，以逗号隔开
     */
    public void setKinds(String kinds) {
        this.kinds = kinds;
    }

    /**
     * 获取电影封面图
     *
     * @return cover - 电影封面图
     */
    public String getCover() {
        return cover;
    }

    /**
     * 设置电影封面图
     *
     * @param cover 电影封面图
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * 获取统计播放次数
     *
     * @return play_count - 统计播放次数
     */
    public Long getPlayCount() {
        return playCount;
    }

    /**
     * 设置统计播放次数
     *
     * @param playCount 统计播放次数
     */
    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }

    /**
     * 获取电影上映年代
     *
     * @return show_year - 电影上映年代
     */
    public Date getShowYear() {
        return showYear;
    }

    /**
     * 设置电影上映年代
     *
     * @param showYear 电影上映年代
     */
    public void setShowYear(Date showYear) {
        this.showYear = showYear;
    }

    /**
     * 获取创建、上传时间
     *
     * @return create_time - 创建、上传时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建、上传时间
     *
     * @param createTime 创建、上传时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}