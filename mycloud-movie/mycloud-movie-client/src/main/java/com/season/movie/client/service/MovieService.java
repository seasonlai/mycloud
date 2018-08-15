package com.season.movie.client.service;

import com.season.movie.dao.entity.Movie;
import com.season.movie.dao.mapper.MovieMapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.weekend.Weekend;

import java.util.List;

/**
 * Created by Administrator on 2018/8/1.
 */
@Service
public class MovieService {

    @Autowired
    MovieMapper movieMapper;


    public List<Movie> listHot(int offset,int limit) {
        Weekend<Movie> weekend = Weekend.of(Movie.class);
        weekend.orderBy("playCount").desc();
        return movieMapper.selectByExampleAndRowBounds(weekend, new RowBounds(offset, limit));
    }

    public List<Movie> listNew(int offset, int limit) {

        Weekend<Movie> weekend = Weekend.of(Movie.class);
        weekend.orderBy("playCount").desc();
        return movieMapper.selectByExampleAndRowBounds(weekend, new RowBounds(offset, limit));
    }

    public List<Movie> listClassic(int offset, int limit) {

        Weekend<Movie> weekend = Weekend.of(Movie.class);
        weekend.orderBy("playCount").asc();
        return movieMapper.selectByExampleAndRowBounds(weekend, new RowBounds(offset, limit));
    }
}
