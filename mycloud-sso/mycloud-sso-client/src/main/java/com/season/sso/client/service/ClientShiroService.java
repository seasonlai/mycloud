package com.season.sso.client.service;

import com.season.common.web.config.AppConfig;
import com.season.common.web.util.SpringUtil;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/10.
 */
public class ClientShiroService extends AbstractShiroService {
    @Autowired
    private AppConfig appConfig;

    @Autowired
    SSOService ssoService;

    static Logger logger = LoggerFactory.getLogger(ClientShiroService.class);

    @Override
    protected ShiroFilterFactoryBean getShiroFilterFactoryBean() {

        return SpringUtil.getBean(ShiroFilterFactoryBean.class);
    }

    /**
     * 获取化权限
     */
    @Override
    protected Map<String, String> loadFilterChainDefinitions() {
        // 权限控制map.从数据库获取
        Map<String, String> chainDefinition = ssoService.selectAllChainDefinition(appConfig.getAppCode());
        if (Objects.isNull(chainDefinition)) {
            chainDefinition = new LinkedHashMap<>(3);
        }
        chainDefinition.put("/favicon.ico","anon");
        chainDefinition.put("/**", "authc");

        if(logger.isDebugEnabled()){
            logger.debug("应用：{}, 权限列表为: ",appConfig.getName());
            chainDefinition.forEach((k,v)->{
                logger.debug("{} -> {}",k,v);
            });
        }

        return chainDefinition;
    }

}
