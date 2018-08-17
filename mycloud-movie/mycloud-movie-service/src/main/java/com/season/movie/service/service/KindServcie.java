package com.season.movie.service.service;

import com.season.common.web.base.BaseService;
import com.season.movie.dao.entity.Kind;
import com.season.movie.dao.mapper.KindMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018/8/17.
 */
@Service
public class KindServcie extends BaseService{

    @Autowired
    KindMapper kindMapper;

    public List<Kind> listAll(){
        return kindMapper.selectAll();
    }


}
