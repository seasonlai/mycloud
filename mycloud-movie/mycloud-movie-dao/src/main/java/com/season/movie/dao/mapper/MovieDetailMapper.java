package com.season.movie.dao.mapper;


import com.season.movie.dao.MyMapper;
import com.season.movie.dao.entity.MovieDetail;

public interface MovieDetailMapper extends MyMapper<MovieDetail> {

    MovieDetail selectByMovieId(Long id);

}