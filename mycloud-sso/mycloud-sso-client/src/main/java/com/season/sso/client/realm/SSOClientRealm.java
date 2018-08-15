package com.season.sso.client.realm;

import com.season.common.base.BaseException;
import com.season.sso.client.model.LoginPermission;
import com.season.sso.client.model.LoginRole;
import com.season.sso.client.model.LoginUser;
import com.season.sso.client.service.SSOService;
import com.season.sso.client.util.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 用户认证和授权
 * Created by shuzheng on 2017/1/20.
 */
public class SSOClientRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(SSOClientRealm.class);

    @Autowired
    private SSOService SSOService;

    /**
     * 授权：验证权限时调用
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
//
        // 当前用户所有角色
        List<LoginRole> roles = SSOService.selectRoleByUsername(username);

        Set<String> roleStr = new HashSet<>();
        Set<String> permissionStr = new HashSet<>();
        if (!CollectionUtils.isEmpty(roles)) {
            for (LoginRole role : roles) {
                roleStr.add(role.getRoleName());
                Set<LoginPermission> permissions = role.getPermissions();
                if (!CollectionUtils.isEmpty(permissions)) {
                    for (LoginPermission permission : permissions) {
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
        Subject subject = SecurityUtils.getSubject();
        LoginUser loginUser = ShiroUtil.getLoginUser(subject.getSession());
        if (Objects.isNull(loginUser)) {
            throw new AccountException("取不到登录对象？假的登录？");
        }
        //获取用户信息
//        String name = authenticationToken.getPrincipal().toString();
//        String username = authenticationToken.getCredentials().toString();
//        LoginUser user = new LoginUser();
//        user.setName(name);
        if (logger.isDebugEnabled()) {
            logger.debug("执行无密登陆：用户-{}", loginUser.getName());
        }
        return new SimpleAuthenticationInfo(loginUser, "", getName());

    }

}
