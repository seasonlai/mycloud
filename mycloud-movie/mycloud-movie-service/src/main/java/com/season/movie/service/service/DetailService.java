package com.season.movie.service.service;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;
import com.season.common.web.base.BaseService;
import com.season.movie.dao.entity.Kind;
import com.season.movie.dao.entity.Movie;
import com.season.movie.dao.entity.MovieDetail;
import com.season.movie.dao.entity.Video;
import com.season.movie.dao.mapper.KindMapper;
import com.season.movie.dao.mapper.MovieDetailMapper;
import com.season.movie.dao.mapper.MovieMapper;
import com.season.movie.dao.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/2.
 */
@Service
public class DetailService extends BaseService {

    @Autowired
    MovieMapper movieMapper;

    @Autowired
    MovieDetailMapper detailMapper;

    public Map getDetail(Long id) {
        throwExceptionIfExistNull(id);
        Movie movie = movieMapper.selectByMovieIdWithKind(id);
        if (Objects.isNull(movie)) {
            throw new BaseException(ResultCode.DAO_ERROR, "资源不存在");
        }
        MovieDetail movieDetail = detailMapper.selectByMovieId(id);

        Map<String, Object> map = new HashMap<>(6);
        map.put("info", movie);
        map.put("detail", movieDetail);
        return map;
    }

}
