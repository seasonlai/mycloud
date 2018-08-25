package com.season.movie.client.controller;

import com.season.movie.dao.entity.Movie;
import com.season.movie.dao.entity.MovieDetail;
import com.season.movie.dao.entity.Video;
import com.season.movie.dao.mapper.MovieDetailMapper;
import com.season.movie.service.service.DetailService;
import com.season.movie.service.service.MovieService;
import com.season.movie.service.service.VideoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

/**
 * Created by Administrator on 2018/8/2.
 */
@Api("播放视频")
@Controller
@RequestMapping("/play")
public class PlayController {

    @Autowired
    VideoService videoService;
    @Autowired
    MovieService movieService;

    @GetMapping("/{movieId}")
    public String playPage(@PathVariable("movieId") Long movieId, Model model) {
        Movie movie = movieService.findById(movieId);
        model.addAttribute("movie", movie);
        Video video = null;
        if (!Objects.isNull(movie))
            video = videoService.findById(movie.getId());
        model.addAttribute("video", video);
        return "play";
    }

}
