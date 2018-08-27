package com.season.movie.dao.mapper;


import com.season.movie.dao.MyMapper;
import com.season.movie.dao.entity.Movie;

import java.util.List;

public interface MovieMapper extends MyMapper<Movie> {

    public Movie selectByMovieIdWithKind(Long movieId);

    public List<Movie> selectAllWithKind();
}