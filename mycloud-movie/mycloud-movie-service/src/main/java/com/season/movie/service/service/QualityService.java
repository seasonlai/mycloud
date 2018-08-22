package com.season.movie.service.service;

import com.season.common.web.base.BaseService;
import com.season.movie.dao.entity.Quality;
import com.season.movie.dao.mapper.QualityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018/8/22.
 */
@Service
public class QualityService extends BaseService{

    @Autowired
    QualityMapper qualityMapper;

    public List<Quality> listAll(){
        return qualityMapper.selectAll();
    }


}
