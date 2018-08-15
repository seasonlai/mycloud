package com.season.sso.server.controller.admin;

import com.season.common.base.BaseResult;
import com.season.sso.server.controller.BaseController;
import com.season.sso.server.service.RoleService;
import com.season.sso.server.service.UserRoleService;
import com.season.sso.server.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/7/28.
 */
@Api("用户和角色相关api")
@RestController
@RequestMapping("/admin/user_role/")
public class UserRoleController extends BaseController {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    UserRoleService userRoleService;

    @PostMapping("list")
    public BaseResult list(@RequestParam("appId") Integer appId, @RequestParam("userId") Long userId) {
        return BaseResult.successData(userRoleService.list(appId, userId));
    }


    @PostMapping("update")
    public BaseResult update(@RequestParam("appId") Integer appId,
                             @RequestParam("userId") Long userId
            , @RequestParam("roles") String roleIds) {
        userRoleService.updateRoles(appId,userId,roleIds);
        return BaseResult.success(null);
    }


}
