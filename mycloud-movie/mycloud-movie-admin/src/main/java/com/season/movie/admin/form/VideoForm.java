package com.season.movie.admin.form;

import com.season.movie.dao.entity.Quality;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/21.
 */
public class VideoForm {

    @NotNull
    private String videoName;
    private String videoDesc;
    @NotNull
    private Quality videoQuality;

    private Date startTime;
    private Date endTime;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
