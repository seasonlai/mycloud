package com.season.sso.server.controller.rpc;

import com.season.common.base.BaseResult;
import com.season.sso.client.constant.Constant;
import com.season.sso.client.model.LoginPermission;
import com.season.sso.client.model.LoginRole;
import com.season.sso.server.entity.Permission;
import com.season.sso.server.entity.Role;
import com.season.sso.server.entity.User;
import com.season.sso.server.service.PermissionService;
import com.season.sso.server.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Administrator on 2018/8/11.
 */
@Api("远程调用接口")
@RestController
@RequestMapping("/sso-service")
public class RPCController {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    PermissionService permissionService;

    @Autowired
    private Constant constant;

    @ApiOperation(value = "校验token")
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public Object code(HttpServletRequest request) {
        String tokenParam = request.getParameter("token");
        String token = redisTemplate.opsForValue()
                .get(constant.getKeyPrefix()+constant.getMYCLOUD_SERVER_CODE() + tokenParam);
        if (StringUtils.isEmpty(token) || !token.equals(tokenParam)) {
            return new BaseResult(-1, "无效token");
        }
        return BaseResult.successData(token);
    }

    @ApiOperation(value = "查询用户角色")
    @RequestMapping(value = "/roles", method = RequestMethod.POST)
    public BaseResult getUserRole(@RequestParam("username") String username) {

        User user = userService.findByUserName(username);

        if (Objects.isNull(user)) {
            return new BaseResult(-1, "用户不存在");
        }
        Set<Role> roles = user.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            List<LoginRole> loginRoles = new ArrayList<>(roles.size());
            for (Role role : roles) {
                LoginRole loginRole = new LoginRole();

                loginRole.setRoleId(role.getRoleId());
                loginRole.setRoleName(role.getRoleName());
                Set<Permission> permissions = role.getPermissions();
                if (!CollectionUtils.isEmpty(permissions)) {
                    Set<LoginPermission> loginPermissions = new HashSet<>(permissions.size());
                    for (Permission permission : permissions) {
                        LoginPermission loginPermission = new LoginPermission();
                        loginPermission.setPermissionId(permission.getPermissionId());
                        loginPermission.setPermissionName(permission.getPermissionName());
                        loginPermission.setPermissionNeed(permission.getPermissionNeed());
                        loginPermissions.add(loginPermission);
                    }
                    loginRole.setPermissions(loginPermissions);
                }
                loginRoles.add(loginRole);
            }
            return BaseResult.successData(loginRoles);
        }


        return BaseResult.success();
    }

//    @ApiOperation(value = "查询用户角色")
//    @RequestMapping(value = "/user", method = RequestMethod.POST)
//    public BaseResult getUser(@RequestParam("username") String username) {
//
//        User user = userService.findByUserName(username);
//
//        if (Objects.isNull(user)) {
//            return new BaseResult(-1, "用户不存在");
//        }
//        return BaseResult.successData(user);
//    }


    @ApiOperation(value = "查询应用过滤权限")
    @RequestMapping(value = "/appChainDefinition", method = RequestMethod.POST)
    public BaseResult getAppChainDefinition(@RequestParam("appCode") String appCode) {
        return BaseResult.successData(permissionService.getAppChainDefinition(appCode));
    }

}
