package com.season.movie.service.service;

import com.season.common.web.base.BaseService;
import com.season.movie.dao.entity.Video;
import com.season.movie.dao.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2018/8/22.
 */
@Service
public class VideoService extends BaseService {

    @Autowired
    VideoMapper videoMapper;


    public void addVideo(Video video) {
        throwExceptionIfExistNull(video);
        videoMapper.insert(video);
    }

}
