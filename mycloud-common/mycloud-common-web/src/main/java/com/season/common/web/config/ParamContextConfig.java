package com.season.common.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * servlet启动事件监听
 */
@WebListener
public class ParamContextConfig implements ServletContextListener {

    static Logger logger = LoggerFactory.getLogger(ParamContextConfig.class);

    @Value("${mycloud.static.url:null}")
    private String static_path;
    @Value("${mycloud.static.image.url:null}")
    private String static_img_path;

    @Autowired
    private AppConfig appConfig;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        context.setAttribute("_staticPath", static_path);
        context.setAttribute("_imgPath", static_img_path);
        context.setAttribute("_realPath", context.getRealPath("/"));
        context.setAttribute("_appName", appConfig.getName());
        logger.info("ServletContext初始化参数完成，如：_staticPath：{}", static_path);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
