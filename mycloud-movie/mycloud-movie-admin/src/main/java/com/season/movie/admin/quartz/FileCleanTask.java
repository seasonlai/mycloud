package com.season.movie.admin.quartz;

import com.github.pagehelper.PageHelper;
import com.season.common.web.util.WebFileUtils;
import com.season.movie.dao.entity.Task;
import com.season.movie.dao.enums.TaskStatus;
import com.season.movie.dao.mapper.TaskMapper;
import com.season.movie.dao.mapper.VideoMapper;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.RowBounds;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.io.File;
import java.util.List;

public class FileCleanTask extends QuartzJobBean {

    static Logger logger = LoggerFactory.getLogger(FileCleanTask.class);

    @Value("${file.tmp.path:}")
    private String tmpPath;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        logger.info("执行清理文件任务");

        Weekend<Task> taskWeekend = Weekend.of(Task.class);
        WeekendCriteria<Task, Object> taskWeekendCriteria = taskWeekend.weekendCriteria();
        taskWeekendCriteria.andEqualTo(Task::getStatus, TaskStatus.TRASH);
        PageHelper.startPage(0,50);
        List<Task> taskList = taskMapper.selectByExample(taskWeekend);
        taskList.forEach(task -> {
            String filePath = task.getFilePath();
            File file = new File(filePath);
            if(file.exists()){
                if(FileUtils.deleteQuietly(file)){
                    task.setStatus(TaskStatus.DONE);
                    taskMapper.updateByPrimaryKeySelective(task);
                }
            }else{
                file=new File(tmpPath,task.getFilePath());
                if (file.exists()&&FileUtils.deleteQuietly(file)){
                    task.setStatus(TaskStatus.DONE);
                    taskMapper.updateByPrimaryKeySelective(task);
                }else {
//                    task.set
                }
            }
        });

    }
}