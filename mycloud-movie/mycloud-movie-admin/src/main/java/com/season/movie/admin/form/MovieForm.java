package com.season.movie.admin.form;

import com.season.movie.dao.entity.Movie;
import com.season.movie.dao.entity.MovieDetail;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/16.
 */
public class MovieForm {

    private Long movieId;
    //封面
    private String movieImg;
    @NotBlank
    private String movieName;
    //类型
    @NotNull
    private String movieKind;
    //上映时间
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date showTime;

    @Digits(integer = 99, fraction = 99)
    private BigDecimal price;

    private String director;

    private String actors;

    private String keyword;
    //简介
    private String description;

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getMovieImg() {
        return movieImg;
    }

    public void setMovieImg(String movieImg) {
        this.movieImg = movieImg;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieKind() {
        return movieKind;
    }

    public void setMovieKind(String movieKind) {
        this.movieKind = movieKind;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Movie movie() {
        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setPrice(price);
        movie.setName(movieName);
        movie.setCover(movieImg);
        movie.setShowYear(showTime);
        return movie;
    }

    public MovieDetail movieDetail() {
        MovieDetail movieDetail1 = new MovieDetail();
        movieDetail1.setMovieId(movieId);
        movieDetail1.setActors(actors);
        movieDetail1.setDescription(description);
        movieDetail1.setKeyword(keyword);
        movieDetail1.setDirector(director);
        return movieDetail1;
    }
}
