package com.season.sso.server.service;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;
import com.season.sso.client.jms.SSOClientJMS;
import com.season.sso.client.util.ShiroUtil;
import com.season.common.web.base.BaseService;
import com.season.sso.server.entity.Role;
import com.season.sso.server.entity.User;
import com.season.sso.server.repository.RoleRepository;
import com.season.sso.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by Administrator on 2018/7/24.
 */
@Service
public class RoleService extends BaseService {


    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SSOClientJMS ssoClientJMS;

    public Page<Role> findRoleByPage(int pageNum, int pageSize, Integer appId, String roleName) {
        PageRequest pageParam = PageRequest.of(pageNum, pageSize);
        Specification<Role> spec = new Specification<Role>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder cb) {
                //关联查询指定应用的角色
                Predicate p = cb.equal(root.get("roleAppId"), appId);
                if (!StringUtils.isEmpty(roleName)) {
                    Path<String> name = root.get("roleName");
                    Predicate like = cb.like(name, roleName);
                    p = cb.and(p, like);
                }
                return p;
            }
        };
        return roleRepository.findAll(spec, pageParam);
    }

    public void updateEnable(List<Role> roles, int status) {

        if (status < 0 || status > 1) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, "锁定参数不合法");
        }
        Set<Integer> ids = new HashSet<>();

        Iterator<Role> iterator = roles.iterator();
        Set<Role> updateRoles = new HashSet<>();
        while (iterator.hasNext()){
            Role role = iterator.next();
            if (role.getEnable() != status) {
                ids.add(role.getRoleId());
                updateRoles.add(role);
            }
        }
        try {
            if (!CollectionUtils.isEmpty(ids)) {
                roleRepository.updateEnable(ids, (byte) status);

                //更新权限
                Set<User> updateUsers = userRepository.findByRolesIn(updateRoles);
                for (User updateUser : updateUsers) {
                    ssoClientJMS.updateUser(updateUser.getUserName());
                }
            }
        } catch (Throwable t) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "启用或停用角色失败", t);
        }

    }

    public void edit(Role role, int option) {
        throwExceptionIfExistNull(role, role.getRoleName(), role.getSort(), role.getEnable());
        switch (option) {
            case 1:
                role.setRoleId(null);
                roleRepository.save(role);
                break;

            case 2:
                if (Objects.isNull(role.getRoleId()))
                    throw new BaseException(ResultCode.VALIDATE_ERROR, "角色ID为空");
                roleRepository.updateByRoleId(role);
                break;
            default:
                throw new BaseException(ResultCode.VALIDATE_ERROR, "不合法操作类型");
        }
    }

    public void delete(List<Role> roles) {
        try {
            roleRepository.deleteInBatch(roles);
            Set<User> updateUsers = userRepository.findByRolesIn(roles);
            if (!CollectionUtils.isEmpty(updateUsers)) {
                for (User updateUser : updateUsers) {
                    ShiroUtil.kickOutUser(updateUser.getUserId(), false);
                }
            }
        } catch (Throwable t) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "删除角色失败", t);
        }
    }
}
