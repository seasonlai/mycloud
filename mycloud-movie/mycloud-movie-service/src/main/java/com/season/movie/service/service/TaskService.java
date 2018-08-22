package com.season.movie.service.service;

import com.season.common.web.base.BaseService;
import com.season.movie.dao.entity.Quality;
import com.season.movie.dao.entity.Task;
import com.season.movie.dao.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/22.
 */
@Service
public class TaskService extends BaseService {

    @Autowired
    TaskMapper taskMapper;

    public Object listAll() {
        List<Task> tasks = taskMapper.selectAll();
        HashMap<String, List<Task>> taskMap = new HashMap<>(tasks.size() + 3);
        tasks.forEach(task -> {
            List<Task> taskList = null;
            boolean toAdd = true;
            switch (task.getStatus()) {
                case TRASH:
                    taskList = taskMap.get("trash");
                    break;
                case DONE:
                    taskList = taskMap.get("done");
                    break;
                case UNFINISH:
                    taskList = taskMap.get("unfinish");
                    break;
                default:
                    toAdd = false;
            }
            if (toAdd) {
                if (Objects.isNull(taskList)) {
                    taskList = new ArrayList<>();
                }
                taskList.add(task);
            }
        });

        return taskMap;
    }

    public void add(Task task){
        throwExceptionIfExistNull(task);
        taskMapper.insert(task);
    }

}
