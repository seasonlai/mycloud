package com.season.sso.server.controller.admin;

import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.validator.Validator;
import com.season.common.validator.annotation.ValidateParam;
import com.season.sso.server.controller.BaseController;
import com.season.sso.server.entity.User;
import com.season.sso.server.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Administrator on 2018/7/20.
 */
@Api("管理员用户管理")
@RestController
@RequestMapping("/admin/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ModelAndView index() {
        return new ModelAndView("/admin/user");
    }

    @ApiOperation(value = "获取应用列表", httpMethod = "POST")
    @PostMapping("/list")
    public BaseResult list(
            @RequestParam(value = "pageNum", defaultValue = "0")
            @ValidateParam(Validator.INT) String pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10")
            @ValidateParam(Validator.INT) String pageSize,
            @RequestParam(value = "userName", required = false)
                    String userName) {
        Page<User> page = userService.findAdminByPage(Integer.valueOf(pageNum)
                , Integer.valueOf(pageSize),userName);
        return BaseResult.successData(page);
    }

    @ApiOperation(value = "锁定或解锁用户", httpMethod = "POST")
    @PostMapping("/lockOrUnlock")
    public BaseResult lockOrUnlock(@RequestBody List<User> users,@RequestParam("status") Integer lockStatus){
        if(CollectionUtils.isEmpty(users)){
            return new BaseResult(ResultCode.VALIDATE_ERROR,"无待操作的用户");
        }
        userService.lockOrUnlock(users,lockStatus);
        return BaseResult.success("操作成功");
    }

    @ApiOperation(value = "编辑用户", httpMethod = "POST")
    @PostMapping("/edit")
    public BaseResult edit(User user,@Param("option") Integer option){
        userService.edit(user,option);
        return BaseResult.success("操作成功");
    }

    @ApiOperation(value = "编辑用户", httpMethod = "POST")
    @PostMapping("/del")
    public BaseResult del(List<User> users){
        userService.del(users);
        return BaseResult.success("操作成功");
    }
}

