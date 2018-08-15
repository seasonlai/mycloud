package com.season.sso.server.shiro;

import com.season.common.web.captcha.CaptchaFilter;
import com.season.common.web.config.AppConfig;
import com.season.sso.client.constant.Constant;
import com.season.sso.client.filter.CustomAuthFilter;
import com.season.sso.client.filter.PermsFilter;
import com.season.sso.server.entity.App;
import com.season.sso.server.entity.Permission;
import com.season.sso.server.repository.AppRepository;
import com.season.sso.server.repository.PermissionRepository;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/10.
 */
@Configuration
public class SSOServerConfiguration {

    static Logger logger = LoggerFactory.getLogger(SSOServerConfiguration.class);

    @Value("${shiro.cache.authenticationCacheName:authenticationCache}")
    private String authenticationCacheName;
    @Value("${shiro.cache.authorizationCacheName:authorizationCache}")
    private String authorizationCacheName;

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private AppRepository appRepository;

    @Bean("ssoServerRealm")
    public SSOServerRealm ssoServerRealm(CacheManager cacheManager) {
        SSOServerRealm realm = new SSOServerRealm();
        //设置密码验证机制
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName(Constant.HASH_ALGORITHM);
        matcher.setHashIterations(Constant.HASH_ITERATION);
        realm.setCredentialsMatcher(matcher);
        //设置缓存
        realm.setAuthenticationCachingEnabled(true);
        realm.setAuthenticationCacheName(authenticationCacheName);
        realm.setAuthorizationCachingEnabled(true);
        realm.setAuthorizationCacheName(authorizationCacheName);
        realm.setCacheManager(cacheManager);//放最后，不然名字失效
        return realm;
    }

    @Bean()
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
            DefaultWebSecurityManager securityManager,
            CustomAuthFilter authFilter, PermsFilter permsFilter,
            @Qualifier("ssoServerRealm")Realm realm) {

        securityManager.setRealm(realm);

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 添加自己的过滤器并且取名为
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", authFilter);
        filterMap.put("captcha", new CaptchaFilter());
        filterMap.put("perms", permsFilter);
        shiroFilterFactoryBean.setFilters(filterMap);

        Map<String, String> map = new LinkedHashMap<>();
        App app = appRepository.findByAppCode(appConfig.getAppCode());
        if (Objects.isNull(app)) {
            logger.warn("应用未注册，应用码：{}", appConfig.getAppCode());
        } else {
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
                map.put(p.getPermissionUrl(), permissionNeed);
            }
        }
        map.put("/sso-service/**","anon");
        //对所有用户认证
        map.put("/**", "authc");
        //登录
        shiroFilterFactoryBean.setLoginUrl("/sso/login");
        //首页
        shiroFilterFactoryBean.setSuccessUrl("/sso/index");
        //错误页面，认证不通过跳转
        shiroFilterFactoryBean.setUnauthorizedUrl("/sso/error");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }


}
