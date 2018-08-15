package com.season.movie.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolConfig.class);

    private static final int corePoolSize = 10;
    private static final int maxPoolSize = 200;
    private static final int queueCapacity = 20;

    @Value("${threads.data.coreSize:5}")
    private int data_coreSize;
    @Value("${threads.data.maxSize:10}")
    private int data_maxSize;
    @Value("${threads.data.queueCapacity:10}")
    private int data_queueCapacity;
    @Value("${threads.data.namePrefix:DataExecutor-}")
    private String data_namePrefix;

    @Bean("dataExecutor")
    public Executor dataExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(data_coreSize);
        executor.setMaxPoolSize(data_maxSize);
        executor.setQueueCapacity(data_queueCapacity);
        executor.setThreadNamePrefix(data_namePrefix);
        executor.initialize();
        return executor;
    }

    @Bean("clearExecutor")
    public Executor clearExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("ClearExecutor-");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
