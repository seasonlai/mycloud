package com.season;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by Administrator on 2018/8/13.
 */
@SpringBootApplication
@ServletComponentScan
public class MycloudMovieAdminApplication {


    public static void main(String[] args) {
        SpringApplication.run(MycloudMovieAdminApplication.class, args);
    }

}
