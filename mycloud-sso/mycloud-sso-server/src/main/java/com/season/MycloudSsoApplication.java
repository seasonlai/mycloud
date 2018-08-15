package com.season;

import com.season.common.util.key.SnowflakeIdWorker;
import com.season.common.web.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

import java.util.Collections;


@SpringBootApplication
@ServletComponentScan
@EnableEurekaClient
public class MycloudSsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MycloudSsoApplication.class, args);
    }


    /**
     * 分布式ID唯一性
     *
     * @return
     */
    @Bean
    public SnowflakeIdWorker getWorker(AppConfig appConfig) {
        return new SnowflakeIdWorker(appConfig.getWorkerId(), appConfig.getDataCenterId());
    }


    /**
     * 解决JPA的no session问题
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean openEntityViewFilter() {
        FilterRegistrationBean<OpenEntityManagerInViewFilter> bean = new FilterRegistrationBean();
        bean.setFilter(new OpenEntityManagerInViewFilter());
        bean.setUrlPatterns(Collections.singletonList("/*"));
        bean.setName("openEntityManagerInViewFilter");
        bean.setOrder(Integer.MAX_VALUE);
        return bean;
    }

}
