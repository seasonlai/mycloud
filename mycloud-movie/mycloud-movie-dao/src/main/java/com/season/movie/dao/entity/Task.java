package com.season.movie.dao.entity;

import com.season.movie.dao.enums.TaskStatus;

import javax.persistence.*;

public class Task {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 文件路径
     */
    @Column(name = "filePath")
    private String filepath;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 进度（断点续传）
     */
    private Long process;

    /**
     * 状态
     */
    private TaskStatus status;

    /**
     * 类型: 0-上传 1-下载
     */
    private Byte kind;

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
     * 获取任务名称
     *
     * @return name - 任务名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置任务名称
     *
     * @param name 任务名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取文件路径
     *
     * @return filePath - 文件路径
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * 设置文件路径
     *
     * @param filepath 文件路径
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    /**
     * 获取文件大小
     *
     * @return size - 文件大小
     */
    public Long getSize() {
        return size;
    }

    /**
     * 设置文件大小
     *
     * @param size 文件大小
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * 获取进度（断点续传）
     *
     * @return process - 进度（断点续传）
     */
    public Long getProcess() {
        return process;
    }

    /**
     * 设置进度（断点续传）
     *
     * @param process 进度（断点续传）
     */
    public void setProcess(Long process) {
        this.process = process;
    }

    /**
     * 获取状态
     *
     * @return status - 状态
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * 获取类型: 0-上传 1-下载
     *
     * @return kind - 类型: 0-上传 1-下载
     */
    public Byte getKind() {
        return kind;
    }

    /**
     * 设置类型: 0-上传 1-下载
     *
     * @param kind 类型: 0-上传 1-下载
     */
    public void setKind(Byte kind) {
        this.kind = kind;
    }
}