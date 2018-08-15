package com.season.sso.server.controller.admin;

import com.season.common.base.BaseResult;
import com.season.sso.server.controller.BaseController;
import com.season.sso.server.entity.App;
import com.season.sso.server.repository.AppRepository;
import com.season.sso.server.service.RolePermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/30.
 */
@Api("角色授权")
@RestController
@RequestMapping("/admin/role_perms")
public class RolePermissionController extends BaseController {

    @Autowired
    AppRepository appRepository;

    @Autowired
    RolePermissionService rolePermissionService;

    @ApiOperation("跳转页面")
    @GetMapping
    public ModelAndView index() {
        List<App> apps = appRepository.findAll();
        ModelAndView mav = new ModelAndView("/admin/role_perms");
        mav.addObject("apps", apps);
        return mav;
    }


    @ApiOperation(value = "查询带权限的角色树", httpMethod = "POST")
    @PostMapping("/tree")
    public Object roleTree(@RequestParam("appId") Integer appId) {
        return BaseResult.successData(rolePermissionService.tree(appId));
    }

    @ApiOperation(value = "查询带权限的角色树", httpMethod = "POST")
    @PostMapping("/update")
    public Object update(@RequestBody Map<String,Object> params) {

        rolePermissionService.update(params);

        return BaseResult.success(null);
    }


}
