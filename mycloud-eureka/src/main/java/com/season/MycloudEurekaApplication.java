package com.season;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Created by Administrator on 2018/8/10.
 */
@SpringBootApplication
@EnableEurekaServer
public class MycloudEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MycloudEurekaApplication.class, args);
    }

}
