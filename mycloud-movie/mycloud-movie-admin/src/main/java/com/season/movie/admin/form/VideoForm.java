package com.season.movie.admin.form;

import com.season.movie.dao.entity.Quality;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2018/8/21.
 */
public class VideoForm {

    @NotNull
    private String videoName;
    private String videoDesc;
    @NotNull
    private Quality videoQuality;


    public VideoForm() {
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoDesc() {
        return videoDesc;
    }

    public void setVideoDesc(String videoDesc) {
        this.videoDesc = videoDesc;
    }

    public Quality getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(Quality videoQuality) {
        this.videoQuality = videoQuality;
    }
}
