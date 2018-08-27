package com.season.movie.service.service;

import com.season.MycloudMovieAdminApplication;
import com.season.movie.dao.entity.Video;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by Administrator on 2018/8/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MycloudMovieAdminApplication.class)
public class VideoServiceTest {

    @Autowired
    VideoService videoService;

    @Test
    public void listAllTest() {
    }


}

