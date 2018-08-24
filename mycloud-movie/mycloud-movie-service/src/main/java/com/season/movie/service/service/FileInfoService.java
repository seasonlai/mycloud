package com.season.movie.service.service;

import com.season.common.web.base.BaseService;
import com.season.movie.dao.entity.FileInfo;
import com.season.movie.dao.mapper.FileInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.Objects;

/**
 * Created by Administrator on 2018/8/23.
 */
@Service
public class FileInfoService extends BaseService {

    @Autowired
    FileInfoMapper fileInfoMapper;


    public FileInfo findByMd5(String md5) {
        Weekend<FileInfo> weekend = Weekend.of(FileInfo.class);
        WeekendCriteria<FileInfo, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(FileInfo::getMd5, md5);
        return fileInfoMapper.selectOneByExample(weekend);
    }

    public void add(FileInfo f) {
        throwExceptionIfExistNull(f);
        fileInfoMapper.insert(f);
    }

    public void update(FileInfo f) {
        throwExceptionIfExistNull(f);
        fileInfoMapper.updateByPrimaryKeySelective(f);
    }


}
