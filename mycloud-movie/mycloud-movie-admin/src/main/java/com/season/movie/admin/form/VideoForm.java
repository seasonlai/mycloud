package com.season.movie.admin.form;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2018/8/21.
 */
public class VideoForm {

    @NotNull
    private String videoName;
    private String videoDesc;
    @NotNull
    private String videoQuality;


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

    public String getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(String videoQuality) {
        this.videoQuality = videoQuality;
    }
}
