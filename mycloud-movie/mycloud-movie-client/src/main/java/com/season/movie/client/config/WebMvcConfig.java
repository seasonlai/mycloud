package com.season.movie.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by season on 2018/8/1.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Value("${file.image.path:/images/}")
    private String imgPath;
    @Value("${file.video.path:/videos/}")
    private String videoPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/images/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**","/videos/**")
                .addResourceLocations("file:" + imgPath,"file:" + videoPath);
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("redirect:/movie");
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
//    }
}
