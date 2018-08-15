package com.season.sso.server.shiro;

import com.season.sso.client.model.LoginUser;
import com.season.sso.client.realm.SSOClientRealm;
import com.season.sso.server.entity.Permission;
import com.season.sso.server.entity.Role;
import com.season.sso.server.entity.User;
import com.season.sso.server.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/8/10.
 */
public class SSOServerRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(SSOClientRealm.class);

    @Autowired
    UserService userService;

    /**
     * 授权：验证权限时调用
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        LoginUser loginUser = (LoginUser) principalCollection.getPrimaryPrincipal();
        if (logger.isDebugEnabled())
            logger.debug("执行获取权限doGetAuthorizationInfo：用户-{}", loginUser.getName());

        User user = userService.findByUserName(loginUser.getName());
        if (user == null) {
            return null;
        }
        Set<Role> roles = user.getRoles();
        Set<String> roleStr = new HashSet<>();
        Set<String> permissionStr = new HashSet<>();
        if (!CollectionUtils.isEmpty(roles)) {
            for (Role role : roles) {
                if (role.getEnable() != 1) {//角色未启用
                    continue;
                }
                roleStr.add(role.getRoleName());
                Set<Permission> permissions = role.getPermissions();
                if (!CollectionUtils.isEmpty(permissions)) {
                    for (Permission permission : permissions) {
                        if (permission.getEnable() != 1) {//权限未启用
                            continue;
                        }
                        String permissionNeed = permission.getPermissionNeed();
                        String[] pair = permissionNeed.split("\\[", 2);
                        if (pair.length == 2) {
                            String tmp = pair[1].trim();
                            permissionNeed = tmp.substring(0, tmp.length() - 1);
                        }
                        permissionStr.add(permissionNeed);
                    }
                }
            }
        }

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roleStr);
        authorizationInfo.setStringPermissions(permissionStr);
        return authorizationInfo;
    }

    /**
     * 认证：登录时调用
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        //获取用户信息
        String name = authenticationToken.getPrincipal().toString();

        User user = userService.findByUserName(name);
        if (user == null) {
            //不存在
            return null;
        }
        if (user.getLocked() == 1) {
            throw new LockedAccountException();
        }

        //这里验证authenticationToken和simpleAuthenticationInfo的信息
        SimpleAuthenticationInfo simpleAuthenticationInfo
                = new SimpleAuthenticationInfo(user.generateLoginUser(), user.getPassword(), getName());
        //加盐
//            simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(""));
        return simpleAuthenticationInfo;
    }


}
