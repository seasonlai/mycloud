package com.season.sso.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by Administrator on 2018/7/28.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

/**
 * 添加自定义参数解析器，用于验证框架
 */
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        argumentResolvers.add(new MethodArgumentResolver());
//    }

    /**
     * 设置/的默认地址
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/admin/index");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}
