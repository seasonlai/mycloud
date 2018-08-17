package com.season.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Created by season on 2018/8/1.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Value("${file.image.path:/images/}")
    private String imgPath;
    @Value("${file.tmp.path:/tmp/}")
    private String tmpPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**", "/tmp/**")
                .addResourceLocations("file:" + imgPath, "file:" + tmpPath);

    }
}
