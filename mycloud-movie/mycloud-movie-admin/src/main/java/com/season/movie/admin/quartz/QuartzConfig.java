package com.season.movie.admin.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class QuartzConfig {
    @Bean
    public JobDetail uploadTaskDetail() {
        return JobBuilder.newJob(FileCleanTask.class)
                .withIdentity("fileCleanTask")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail testTaskDetail() {
        return JobBuilder.newJob(TestTask.class)
                .withIdentity("testTask")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger uploadTaskTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0 1/1 * ?");
        return TriggerBuilder.newTrigger().forJob(uploadTaskDetail())
                .withIdentity("fileCleanTask")
                .withSchedule(scheduleBuilder)
                .build();
    }
    @Bean
    public Trigger testTaskTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("*/5 * * * * ?");
        return TriggerBuilder.newTrigger().forJob(testTaskDetail())
                .withIdentity("testTask")
                .withSchedule(scheduleBuilder)
                .build();
    }


}