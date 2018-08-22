package com.season.movie.admin.controller;

import com.season.common.base.BaseException;
import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.movie.dao.entity.Task;
import com.season.movie.dao.enums.TaskStatus;
import com.season.movie.service.service.TaskService;
import com.season.sso.client.model.LoginUser;
import com.season.sso.client.util.SSOClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by Administrator on 2018/8/22.
 */
@Api("任务相关")
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @ApiOperation(value = "添加任务", httpMethod = "POST")
    @PostMapping("/add")
    public Object add(@RequestParam("kind") Byte kind
            , @RequestParam("targetId") Long targetId
            , @RequestParam("name") String name
            , @RequestParam(value = "filePath", required = false) String filePath) {
        LoginUser loginUser = SSOClientUtil.getLoginUser();
        if (Objects.isNull(loginUser)) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "获取用户失败");
        }
        Task task = new Task();
        task.setTargetId(targetId);
        task.setKind(kind);
        task.setUserId(loginUser.getId());
        task.setName(name);
        task.setStatus(TaskStatus.UNFINISH);
        String rPath = UUID.randomUUID().toString();
        if (!StringUtils.isEmpty(filePath)) {
            int index = filePath.lastIndexOf(".");
            if (index > 0) {//等于0也太奇怪了吧
                rPath += filePath.substring(index);
            }
        }
        task.setFilePath(rPath);
        taskService.add(task);
        return BaseResult.successData(task);
    }


}
