package com.season.movie.admin.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by Administrator on 2018/8/30.
 */
public class TestTask extends QuartzJobBean{

    static Logger logger = LoggerFactory.getLogger(TestTask.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        logger.info("---------测试任务");


    }
}
