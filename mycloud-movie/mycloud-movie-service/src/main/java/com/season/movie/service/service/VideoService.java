package com.season.movie.service.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.season.common.web.base.BaseService;
import com.season.movie.dao.entity.Video;
import com.season.movie.dao.mapper.FileInfoMapper;
import com.season.movie.dao.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/22.
 */
@Service
public class VideoService extends BaseService {

    @Autowired
    VideoMapper videoMapper;

    @Autowired
    FileInfoMapper fileInfoMapper;

    public Map listPage(int pageNum, int pageSize, Video video) {
        Page<Object> page = PageHelper.startPage(pageNum, pageSize);
        List<Video> videos = videoMapper.selectAll();
        Map<String, Object> map = new HashMap<>(4);
        map.put("page", page);
        map.put("list", videos);
        return map;
    }

    public void addVideo(Video video) {
        throwExceptionIfExistNull(video);
        videoMapper.insert(video);
    }

    public Video findById(Long id) {
        if (Objects.isNull(id))
            return null;
        Video video = new Video();
        video.setId(id);
        return videoMapper.selectByPrimaryKey(video);
    }

    public void update(Video video) {
        String code = video.getCode();
        if (!Objects.isNull(code)) {
            //更新fileinfo的信息
            Video orginal = videoMapper.selectByPrimaryKey(video);
            if (!StringUtils.isEmpty(orginal.getCode())) {
                fileInfoMapper.updatePointer(orginal.getCode(), -1);
            }
            if (!StringUtils.isEmpty(code)) {
                fileInfoMapper.updatePointer(code, 1);
            }
        }
        videoMapper.updateByPrimaryKeySelective(video);
    }
}
