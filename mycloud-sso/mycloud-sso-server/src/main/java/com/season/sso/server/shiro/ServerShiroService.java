package com.season.sso.server.shiro;

import com.season.common.web.config.AppConfig;
import com.season.sso.client.service.AbstractShiroService;
import com.season.sso.server.entity.App;
import com.season.sso.server.entity.Permission;
import com.season.sso.server.repository.AppRepository;
import com.season.sso.server.repository.PermissionRepository;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ServerShiroService extends AbstractShiroService {

    static Logger logger = LoggerFactory.getLogger(ServerShiroService.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    AppRepository appRepository;


    /**
     * 获取化权限
     */
    @Override
    protected Map<String, String> loadFilterChainDefinitions() {
        // 权限控制map.从数据库获取
        Map<String, String> chainDefinition = new LinkedHashMap<>();
        App app = appRepository.findByAppCode(appConfig.getAppCode());
        if(Objects.isNull(app)){
            logger.warn("获取权限失败，应用码：{}", appConfig.getAppCode());
        }else {
            List<Permission> permissions = permissionRepository
                    .findByPermissionAppIdOrderBySortDescCreateTimeDesc(app.getAppId());
            for (Permission p : permissions) {
                if (StringUtils.isEmpty(p.getPermissionUrl())
                        || p.getEnable() == 0)
                    continue;
                String permissionNeed = p.getPermissionNeed();
//            if (!"anon".equals(permissionNeed) && !"logout".equals(permissionNeed)) {
//                //权限格式
//                permissionNeed = "perms[" + permissionNeed + "]";
//            }
                chainDefinition.put(p.getPermissionUrl(), permissionNeed);
            }
        }

        chainDefinition.put("/sso-service/**","anon");
        //对所有用户认证
        chainDefinition.put("/**", "authc");
        return chainDefinition;
    }


}