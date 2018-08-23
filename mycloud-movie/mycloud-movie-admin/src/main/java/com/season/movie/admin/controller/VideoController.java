package com.season.movie.admin.controller;

import com.season.common.base.BaseException;
import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.movie.admin.form.VideoForm;
import com.season.movie.dao.entity.Video;
import com.season.movie.dao.enums.VideoStatus;
import com.season.movie.service.service.VideoService;
import com.season.sso.client.model.LoginUser;
import com.season.sso.client.util.SSOClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Created by Administrator on 2018/8/22.
 */
@Api("视频相关")
@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    VideoService videoService;

    @ApiOperation(value = "添加视频", httpMethod = "POST", notes = "返回视频ID")
    @PostMapping("/add")
    public Object addVideo(@Validated VideoForm videoForm) {

        LoginUser loginUser = SSOClientUtil.getLoginUser();
        if (Objects.isNull(loginUser)) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "获取用户失败");
        }

        Video video = new Video();
        video.setStatus(VideoStatus.NOT_EXIST);
        video.setName(videoForm.getVideoName());
        video.setDescription(videoForm.getVideoDesc());
        video.setUserId(loginUser.getId());
        video.setQuality(videoForm.getVideoQuality());

        videoService.addVideo(video);

        return BaseResult.successData(video.getId());
    }

    @ApiOperation(value = "更新视频", httpMethod = "POST")
    @PostMapping("/update")
    public Object updateVideo(Video video) {
        videoService.update(video);
        return BaseResult.successData(video);
    }

}
