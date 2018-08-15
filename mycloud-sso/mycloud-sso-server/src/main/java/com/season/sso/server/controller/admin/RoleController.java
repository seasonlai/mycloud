package com.season.sso.server.controller.admin;

import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.sso.server.controller.BaseController;
import com.season.sso.server.entity.App;
import com.season.sso.server.entity.Role;
import com.season.sso.server.repository.AppRepository;
import com.season.sso.server.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Administrator on 2018/7/20.
 */
@Api("角色管理")
@RestController
@RequestMapping("/admin/role")
public class RoleController extends BaseController {

    @Autowired
    RoleService roleService;
    @Autowired
    private AppRepository appRepository;


    @GetMapping
    public ModelAndView index() {
        List<App> apps = appRepository.findAll();
        ModelAndView mav = new ModelAndView("/admin/role");
        mav.addObject("apps", apps);
        return mav;
    }


    @ApiOperation(value = "获取应用列表", httpMethod = "POST")
    @PostMapping("/list")
    public BaseResult list(
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "appId") Integer appId,
            @RequestParam(value = "roleName", required = false) String roleName) {
        Page<Role> page = roleService.findRoleByPage(pageNum
                , pageSize, appId, roleName);
        return BaseResult.successData(page);
    }

    @ApiOperation(value = "启用、停用角色", httpMethod = "POST")
    @PostMapping("/updateEnable")
    public BaseResult updateEnable(@RequestBody List<Role> roles,@RequestParam("status") int status) {
        roleService.updateEnable(roles,status);
        return BaseResult.success();
    }
    @ApiOperation(value = "启用、停用角色", httpMethod = "POST")
    @PostMapping("/edit")
    public BaseResult edit(Role role,@RequestParam("option") int option) {
        roleService.edit(role,option);
        return BaseResult.success();
    }


    @ApiOperation(value = "角色删除", httpMethod = "POST")
    @PostMapping(value = "/delete")
    public BaseResult delete(@RequestBody List<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return new BaseResult(ResultCode.VALIDATE_ERROR, "没有待删除项");
        }
        roleService.delete(roles);
        return BaseResult.success("操作成功");
    }

}
