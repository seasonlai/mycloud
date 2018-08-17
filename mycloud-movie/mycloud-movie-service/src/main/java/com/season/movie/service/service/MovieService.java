package com.season.movie.service.service;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;
import com.season.common.web.util.WebFileUtils;
import com.season.movie.dao.entity.Movie;
import com.season.movie.dao.entity.MovieDetail;
import com.season.movie.dao.mapper.MovieDetailMapper;
import com.season.movie.dao.mapper.MovieMapper;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.weekend.Weekend;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/1.
 */
@Service
public class MovieService {

    static Logger logger = LoggerFactory.getLogger(MovieService.class);

    @Autowired
    MovieMapper movieMapper;
    @Autowired
    MovieDetailMapper movieDetailMapper;


    public List<Movie> listHot(int offset, int limit) {
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

    @Transactional
    public void addMovie(Movie movie, MovieDetail movieDetail, File tmpFileDir, File goalFileDir, String imgUrl) {
        String movieImg = movie.getCover();
        File srcFile = null, goalFile = null;
        boolean needCopy = true;
        //图片名称处理
        if (!StringUtils.isEmpty(movieImg)) {
            movieImg = movieImg.substring(movieImg.lastIndexOf("/") + 1);
            srcFile = new File(tmpFileDir, movieImg);
            if (!srcFile.exists()) {
                logger.error("图片不存在：{}", srcFile.getAbsoluteFile());
                movie.setCover("");
                needCopy = false;
            } else {
                goalFile = new File(goalFileDir, movieImg);
                if (goalFile.exists()) {
                    logger.warn("将会覆盖图片：{}", goalFile.getAbsoluteFile());
                }
                movie.setCover(imgUrl + (imgUrl.endsWith("/") ? "" : "/") + movieImg);
            }
        }
        //开始插入movie
        int count = movieMapper.insert(movie);
        if (count <= 0) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "添加电影失败");
        }
        //添加电影细节
        movieDetail.setId(movie.getId());
        count = movieDetailMapper.insert(movieDetail);
        if (count <= 0) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "添加电影细节失败");
        }
        //拷贝文件
        if (needCopy) {
            try {
                FileUtils.copyFile(srcFile, goalFile);
            } catch (IOException e) {
                logger.error("拷贝文件失败",e);
                throw new BaseException(ResultCode.SERVICE_ERROR, "添加电影封面时失败");
            }
        }
    }
}
