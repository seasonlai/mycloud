package com.season.sso.server.controller.admin;

import com.season.common.base.BaseResult;
import com.season.sso.server.controller.BaseController;
import com.season.sso.server.entity.App;
import com.season.sso.server.entity.Permission;
import com.season.sso.server.repository.AppRepository;
import com.season.sso.server.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Administrator on 2018/7/20.
 */
@Api("权限管理")
@RestController
@RequestMapping("/admin/permission")
public class PermissionController extends BaseController {

    @Autowired
    private AppRepository appRepository;
    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public ModelAndView index() {
        List<App> apps = appRepository.findAll();
        ModelAndView mav = new ModelAndView("/admin/permission");
        mav.addObject("apps", apps);
        return mav;
    }

    @ApiOperation("获取权限树")
    @PostMapping("/tree")
    public BaseResult getTree(@RequestParam("appId") Integer appId, @RequestParam(value = "pid",required = false) Integer parentId) {
        List tree = permissionService.getTree(appId, parentId);
        return BaseResult.successData(tree);
    }

    @ApiOperation("编辑权限")
    @PostMapping("/edit")
    public BaseResult edit(Permission permission, @RequestParam("option")Integer option){
        permissionService.edit(permission,option);
        return BaseResult.success("操作成功");
    }

    @ApiOperation("删除权限")
    @PostMapping("/del")
    public BaseResult del(@RequestParam("ids")String ids,@RequestParam("appCode") Integer appId){
        permissionService.del(ids,appId);
        return BaseResult.success("操作成功");
    }

}
