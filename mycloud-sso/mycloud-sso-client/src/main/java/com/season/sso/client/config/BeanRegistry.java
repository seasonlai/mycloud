package com.season.sso.client.config;

import com.season.common.web.config.AppConfig;
import com.season.sso.client.constant.Constant;
import com.season.sso.client.filter.CustomAuthFilter;
import com.season.sso.client.filter.PermsFilter;
import com.season.sso.client.service.ClientShiroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.CorsRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2018/8/9.
 */
@Configuration("sso-client-beanRegistry")
@EnableAsync
@EnableDiscoveryClient
public class BeanRegistry {

    static Logger logger = LoggerFactory.getLogger(BeanRegistry.class);

    @Autowired
    ClientShiroService clientShiroService;

    @Autowired
    private AppConfig appConfig;

    @Bean(name = "ssoClientRest")
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ClientShiroService clientShiroService() {
        return new ClientShiroService();
    }

    /**
     * 防止自定义CustomAuthFilter加入servlet的filterChain或起作用
     */
    @Bean
    public FilterRegistrationBean authRegistration(CustomAuthFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public Constant constant(){
        return new Constant();
    }

    /**
     * 防止自定义PermsFilter加入servlet的filterChain或起作用
     */
    @Bean
    public FilterRegistrationBean permsRegistration(PermsFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * 应用启动后向认证中心加载权限
     * @return
     */
    @ConditionalOnProperty(name = "mycloud.sso.type", havingValue = "client", matchIfMissing = true)
    @Bean("updateShiroListener")
    public ApplicationListener<ApplicationStartedEvent> applicationListener(){
        return new ApplicationListener<ApplicationStartedEvent>() {
            @Override
            public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
                logger.info("{}启动完成...",appConfig.getAppCode());
                logger.info("{}开始初始化权限过滤...",appConfig.getAppCode());
                try {
                    clientShiroService.updatePermission();
                }catch (Throwable t){
                    logger.error(appConfig.getAppCode()+"加载权限过滤失败：",t);
                }
            }
        };
    }


    @Value("${shiro.thread.pool.corePoolSize:2}")
    private int corePoolSize;
    @Value("${shiro.thread.pool.maxPoolSize:5}")
    private int maxPoolSize;
    @Value("${shiro.thread.pool.queueCapacity:20}")
    private int queueCapacity;

    @Bean("shiroExecutor")
    public ThreadPoolTaskExecutor shiroExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("shiroExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.initialize();
        return executor;
    }
}
