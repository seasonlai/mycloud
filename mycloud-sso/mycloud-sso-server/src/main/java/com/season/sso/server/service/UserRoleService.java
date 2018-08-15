package com.season.sso.server.service;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;
import com.season.sso.client.util.ShiroUtil;
import com.season.sso.server.dto.UserRole;
import com.season.sso.server.entity.Role;
import com.season.sso.server.entity.User;
import com.season.sso.server.jms.SSOServerJMS;
import com.season.sso.server.repository.RoleRepository;
import com.season.sso.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by season on 2018/7/30.
 */
@Service
public class UserRoleService {


    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    SSOServerJMS ssoServerJMS;


    public List list(Integer appId, Long userId) {
        try {

            User user = userRepository.findById(userId).get();
            Set<Role> roles = user.getRoles();
            List<Role> allRoles = roleRepository.findByRoleAppId(appId);
            return mergin(roles, allRoles);
        } catch (Throwable t) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "查询角色列表失败", t);
        }
    }

    public List mergin(Set<Role> userRoles, List<Role> roles) {
        List<UserRole> result = new ArrayList<>(roles.size());
        for (Role role : roles) {
            UserRole item = new UserRole(role.getRoleId(), role.getRoleName(), false);
            result.add(item);
            for (Iterator<Role> iterator = userRoles.iterator(); iterator.hasNext(); ) {
                Role userRole = iterator.next();
                if (userRole.getRoleId().equals(role.getRoleId())) {
                    iterator.remove();
                    item.setAuthor(true);
                }
            }
        }
        return result;
    }

    @Transactional
    public void updateRoles(Integer appId, Long userId, String nowRoleId) {
        try {
            User user = userRepository.findById(userId).get();
            Set<Role> preRoles = user.getRoles();
            if (StringUtils.isEmpty(nowRoleId)) {
                preRoles.clear();
            } else {
                String[] ids = nowRoleId.split(",");
                List<Integer> idList = new ArrayList<>(ids.length);
                for (String tmp : ids) {
                    Integer id = Integer.parseInt(tmp);
                    idList.add(id);
                }
                Set<Role> nowRoles = roleRepository.findByRoleAppIdAndRoleIdIn(appId, idList);
                user.setRoles(nowRoles);
            }
//            ShiroUtil.kickOutUser(user.getUserName(), false);
            ssoServerJMS.updateUser(user.getUserName());
        } catch (Throwable t) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "更新用户角色失败", t);
        }
    }
}
