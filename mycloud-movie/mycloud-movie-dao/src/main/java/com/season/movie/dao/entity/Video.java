package com.season.movie.dao.entity;

import com.season.movie.dao.enums.VideoStatus;

import javax.persistence.*;
import java.util.Date;

public class Video {
    /**
     * 视频id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 视频名称
     */
    private String name;

    /**
     * 视频码/文件名
     */
    private String code;

    /**
     * 质量: 高清，标清...
     */
    private String quality;

    /**
     * 状态：可用、不存在、侵权...
     */
    private VideoStatus status;

    private String description;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 创建、上传时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 获取视频id
     *
     * @return id - 视频id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置视频id
     *
     * @param id 视频id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取视频名称
     *
     * @return name - 视频名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置视频名称
     *
     * @param name 视频名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取视频码/文件名
     *
     * @return code - 视频码/文件名
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置视频码/文件名
     *
     * @param code 视频码/文件名
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取质量: 高清，标清...
     *
     * @return quality - 质量: 高清，标清...
     */
    public String getQuality() {
        return quality;
    }

    /**
     * 设置质量: 高清，标清...
     *
     * @param quality 质量: 高清，标清...
     */
    public void setQuality(String quality) {
        this.quality = quality;
    }

    /**
     * 获取状态：可用、不存在、侵权...
     *
     * @return status - 状态：可用、不存在、侵权...
     */
    public VideoStatus getStatus() {
        return status;
    }

    /**
     * 设置状态：可用、不存在、侵权...
     *
     * @param status 状态：可用、不存在、侵权...
     */
    public void setStatus(VideoStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
