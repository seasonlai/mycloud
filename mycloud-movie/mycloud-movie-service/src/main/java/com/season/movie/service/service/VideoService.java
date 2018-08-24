package com.season.movie.service.service;

import com.season.common.web.base.BaseService;
import com.season.movie.dao.entity.Video;
import com.season.movie.dao.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018/8/22.
 */
@Service
public class VideoService extends BaseService {

    @Autowired
    VideoMapper videoMapper;


    public List<Video> listAll() {
        return videoMapper.selectAll();
    }

    public void addVideo(Video video) {
        throwExceptionIfExistNull(video);
        videoMapper.insert(video);
    }

    public void update(Video video) {
        videoMapper.updateByPrimaryKeySelective(video);
    }
}
