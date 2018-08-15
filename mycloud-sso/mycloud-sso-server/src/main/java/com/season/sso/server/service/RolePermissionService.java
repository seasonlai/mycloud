package com.season.sso.server.service;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;
import com.season.sso.client.util.ShiroUtil;
import com.season.common.web.base.BaseService;
import com.season.sso.server.entity.Permission;
import com.season.sso.server.entity.Role;
import com.season.sso.server.entity.User;
import com.season.sso.server.jms.SSOServerJMS;
import com.season.sso.server.repository.PermissionRepository;
import com.season.sso.server.repository.RoleRepository;
import com.season.sso.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by Administrator on 2018/7/30.
 */
@Service
public class RolePermissionService extends BaseService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SSOServerJMS ssoServerJMS;


    public List tree(Integer appId) {
        throwExceptionIfExistNull(appId);
        try {

            List<Role> roles = roleRepository.findByRoleAppId(appId);
            List<Permission> allPermissions
                    = permissionRepository.findByPermissionAppIdAndPermissionNeedLike(appId, "perms[%");
            return buildTree(roles, allPermissions);
        } catch (Throwable t) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "获取角色授权树失败", t);
        }
    }

    private List<Map<String, Object>> buildTree(List<Role> roles, List<Permission> allPermissions) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(roles)) {
            return result;
        }
        for (Role role : roles) {
            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("id", role.getRoleId());
            roleMap.put("name", role.getRoleName());
            roleMap.put("nocheck", true);
            roleMap.put("title", role.getRoleName());
            result.add(roleMap);
            if (CollectionUtils.isEmpty(allPermissions)) continue;
            Set<Permission> authorPerms = role.getPermissions();
            for (Permission permission : allPermissions) {
                Map<String, Object> permsMap = new HashMap<>();
                permsMap.put("id", permission.getPermissionId());
                permsMap.put("pid", role.getRoleId());
                permsMap.put("roleId", role.getRoleId());
                permsMap.put("name", permission.getPermissionName());
                permsMap.put("title", PermissionService.buildTitle(permission));
                boolean checked = false;
                if (!CollectionUtils.isEmpty(authorPerms)) {
                    for (Permission authorPerm : authorPerms) {
                        if (Objects.equals(authorPerm.getPermissionId(), permission.getPermissionId())) {
                            checked = true;
                            break;
                        }
                    }
                }
                permsMap.put("checked", checked);
                result.add(permsMap);
            }
        }

        return result;
    }


    @Transactional
    public void update(Map<String, Object> params) {
        Object app_id = params.get("appId");
        throwExceptionIfExistNull(app_id);
        Integer appId = Integer.parseInt(app_id.toString());

        params.remove("appId");
        Set<String> roleIds = params.keySet();
        List<Role> updateRoles = new ArrayList<>(roleIds.size());
        try {
            for (String key : roleIds) {
                Object permsIds = params.get(key);
                if (StringUtils.isEmpty(permsIds))
                    continue;
                List<Integer> list = stringToIntList(permsIds.toString().split(","));
                Integer roleId = Integer.parseInt(key);
                Role role = roleRepository.findById(roleId).get();
                Set<Permission> permissionList = permissionRepository.findByPermissionIdIn(list);
                role.setPermissions(permissionList);

                updateRoles.add(role);
            }
            Set<User> updateUsers = userRepository.findByRolesIn(updateRoles);
            for (User updateUser : updateUsers) {
                ssoServerJMS.updateUser(updateUser.getUserName());
            }
            ShiroUtil.kickOutAllUser(false);
        } catch (Throwable t) {
            if (t instanceof BaseException) {
                throw t;
            }
            throw new BaseException(ResultCode.SERVICE_ERROR, "更新角色权限失败", t);
        }
    }
}
