package com.season.sso.server.service;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;
import com.season.common.web.config.AppConfig;
import com.season.sso.server.entity.App;
import com.season.sso.server.entity.Permission;
import com.season.sso.server.jms.SSOServerJMS;
import com.season.sso.server.repository.AppRepository;
import com.season.sso.server.repository.PermissionRepository;
import com.season.sso.server.shiro.ServerShiroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by Administrator on 2018/7/24.
 */
@Service
public class PermissionService {

    static Logger logger = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    ServerShiroService serverShiroService;

    @Autowired
    AppRepository appRepository;

    @Autowired
    SSOServerJMS ssoServerJMS;

    @Autowired
    AppConfig appConfig;

    public List getTree(Integer appId, Integer parentId) {
        if (Objects.isNull(appId)) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, "应用参数为空");
        }

        Specification<Permission> sp = new Specification<Permission>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder cb) {
                Predicate p = cb.equal(root.get("permissionAppId"), appId);
                if (!Objects.isNull(parentId)) {
                    p = cb.and(p, cb.equal(root.get("parentId"), parentId));
                }
                return p;
            }
        };
        List<Permission> permissions = permissionRepository.findAll(sp);

        return tranToTree(permissions, Objects.isNull(parentId));
    }

    private List tranToTree(List<Permission> permissions, boolean addRoot) {
        List<Map> list = new ArrayList<>();
        if (addRoot) {
            Map<String, Object> root = new HashMap<>();
            root.put("id", 0);
            root.put("name", "权限树");
            root.put("p_sort", "0");
            list.add(root);
        }
        if (!CollectionUtils.isEmpty(permissions)) {
            for (Permission permission : permissions) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", permission.getPermissionId());
                data.put("pid", permission.getParentId());
                data.put("name", permission.getPermissionName());
                data.put("p_permissionUrl", permission.getPermissionUrl());
                data.put("p_permissionNeed", permission.getPermissionNeed());
                data.put("p_enable", permission.getEnable());
                data.put("p_menu", permission.getMenu());
                data.put("p_sort", permission.getSort());
                data.put("p_description", permission.getDescription());
                data.put("title", buildTitle(permission));
                list.add(data);
            }
        }
        return list;
    }

    public static String buildTitle(Permission permission) {
        if (Objects.isNull(permission))
            return null;
        StringBuilder builder = new StringBuilder();
        builder.append("权限名：").append(permission.getPermissionName()).append("\n")
                .append("权限URL：").append(permission.getPermissionUrl()).append("\n")
                .append("是否启用：").append(permission.getEnable() == 1 ? "是" : "否").append("\n")
                .append("所需权限：").append(permission.getPermissionNeed());
        return builder.toString();
    }

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Transactional
    public void edit(Permission permission, Integer option) {

        if (Objects.isNull(permission) || Objects.isNull(option)) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, "参数为空");
        }

        switch (option) {
            case 1:
                permission.setPermissionId(null);
                permissionRepository.save(permission);
                break;
            case 2:
                permissionRepository.updateById(permission);
                break;
            default:
                throw new BaseException(ResultCode.VALIDATE_ERROR, "不合法的操作类型");
        }
        //shiro更新权限
        serverShiroService.updatePermission();
        //通知子应用更新权限
        Optional<App> appOptional = appRepository.findById(permission.getPermissionAppId());
        if (appOptional.isPresent()) {
            App app = appOptional.get();
            if (!Objects.equals(app.getAppCode(), appConfig.getAppCode())) {
                ssoServerJMS.updateApp(app.getAppCode());
            }
        }
    }

    @Transactional
    public void del(String ids, Integer appId) {

        if (StringUtils.isEmpty(ids)) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, "参数为空");
        }
        String[] items = ids.split(",");
        List<Permission> list = new ArrayList<>(items.length);
        for (String id : items) {
            try {
                Permission p = new Permission();
                p.setPermissionId(Integer.valueOf(id));
                list.add(p);
            } catch (Exception e) {
                throw new BaseException(ResultCode.VALIDATE_ERROR, "不合法的参数：" + id);
            }
        }
        permissionRepository.deleteInBatch(list);

        //shiro更新权限
        serverShiroService.updatePermission();
        Optional<App> appOptional = appRepository.findById(appId);
        if (appOptional.isPresent()) {
            App app = appOptional.get();
            if (!Objects.equals(app.getAppCode(), appConfig.getAppCode())) {
                ssoServerJMS.updateApp(app.getAppCode());
            }
        }

    }

    public Map getAppChainDefinition(String appCode) {
        Map<String, String> map = new LinkedHashMap<>();
        if (StringUtils.isEmpty(appCode))
            return map;
        App app = appRepository.findByAppCode(appCode);
        if (Objects.isNull(app)) {
            logger.warn("应用未注册，应用码：{}", appCode);
        } else {
            List<Permission> permissions = permissionRepository
                    .findByPermissionAppIdOrderBySortDescCreateTimeDesc(app.getAppId());
            if (!CollectionUtils.isEmpty(permissions)) {
                for (Permission p : permissions) {
                    if (StringUtils.isEmpty(p.getPermissionUrl())
                            || p.getEnable() == 0)
                        continue;
                    String permissionNeed = p.getPermissionNeed();
//            if (!"anon".equals(permissionNeed) && !"logout".equals(permissionNeed)) {
//                //权限格式
//                permissionNeed = "perms[" + permissionNeed + "]";
//            }
                    map.put(p.getPermissionUrl(), permissionNeed);
                }
            }
        }
        return map;
    }
}
