package com.season.sso.client.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.season.sso.client.constant.Constant;
import com.season.sso.client.filter.CustomAuthFilter;
import com.season.sso.client.filter.PermsFilter;
import com.season.sso.client.realm.SSOClientRealm;
import com.season.sso.client.service.SSOService;
import com.season.common.web.config.AppConfig;
import com.season.sso.client.session.CustomServletSessionManager;
import com.season.sso.client.session.CustomSessionDao;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/2.
 */
@Configuration
public class SSOClientConfiguration {

    static Logger logger = LoggerFactory.getLogger(SSOClientConfiguration.class);

    @Value("${spring.redis.host:127.0.0.7}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private int port;
    @Value("${spring.redis.timeout:2000}")
    private int timeout;
    @Value("${spring.redis.password:}")
    private String password;

    //----------shiro 配置-----------------
    @Value("${shiro.session.timeout:1800000}")
    private long session_timeout;
    @Value("${shiro.redis.session.expire:-2}")
    private int session_expire;//-2是指和session过期时间一样
    @Value("${shiro.redis.expire:60}")
    private int redis_expire;//单位：秒
    @Value("${shiro.redis.session.keyPrefix:shiro:session:}")
    private String session_keyPrefix;
    @Value("${shiro.redis.keyPrefix:shiro:cache:}")
    private String redis_keyPrefix;
    @Value("${shiro.cookie.name:mycloud_session_id}")
    private String cookie_name;
    @Value("${shiro.cookie.value:/}")
    private String cookie_value;


    @Value("${mycloud.sso.url:/}")
    private String ssoUrl;
    @Autowired
    private AppConfig appConfig;
    @Value("${shiro.cache.authenticationCacheName:authenticationCache}")
    private String authenticationCacheName;
    @Value("${shiro.cache.authorizationCacheName:authorizationCache}")
    private String authorizationCacheName;

    @Autowired
    SSOService ssoService;

    //将自己的验证方式加入容器
    @ConditionalOnProperty(name = "mycloud.sso.type", havingValue = "client", matchIfMissing = true)
    @Bean("ssoClientRealm")
    public SSOClientRealm ssoClientRealm(CacheManager cacheManager) {
        SSOClientRealm realm = new SSOClientRealm();
        //设置缓存
        realm.setAuthenticationCachingEnabled(true);
        realm.setAuthenticationCacheName(authenticationCacheName);
        realm.setAuthorizationCachingEnabled(true);
        realm.setAuthorizationCacheName(authorizationCacheName);
        realm.setCacheManager(cacheManager);//放最后，不然名字失效
        return realm;
    }



    /**
     * 配置shiro redisManager
     * <p>
     * 使用的是shiro-redis开源插件
     * <p>
     * TODO 这个单例没问题？
     *
     * @return
     */
    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setTimeout(timeout);
        redisManager.setPassword(password);
        return redisManager;
    }

//    @ConditionalOnProperty(name = "mycloud.sso.type", havingValue = "server")
    @Bean
    public SessionDAO sessionDAO(RedisManager redisManager) {
        RedisSessionDAO sessionDAO = new CustomSessionDao();
        sessionDAO.setExpire(session_expire);
        sessionDAO.setKeyPrefix(session_keyPrefix);
        sessionDAO.setRedisManager(redisManager);
        return sessionDAO;
    }


    //解决认证中心cookie跨域问题
    @Bean
    public SimpleCookie simpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName(cookie_name);
        simpleCookie.setValue(cookie_value);
//        simpleCookie.setDomain("/");
        return simpleCookie;
    }


    //shiro-redis插件的RedisSessionDAO
//    @ConditionalOnProperty(name = "mycloud.sso.type", havingValue = "server")
    @Bean
    public SessionManager serverSessionManager(SessionDAO sessionDAO, Cookie cookie) {
        logger.info("注入serverSessionManager");
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();//new CustomSessionManager();
        sessionManager.setSessionIdCookie(cookie);
        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setGlobalSessionTimeout(session_timeout);
        sessionManager.setDeleteInvalidSessions(true);
        return sessionManager;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(
            SessionManager sessionManager,
            CacheManager cacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(cacheManager);
        return securityManager;
    }

    @Bean
    public CacheManager cacheManager(RedisManager redisManager) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setKeyPrefix(redis_keyPrefix);
        redisCacheManager.setExpire(redis_expire);
        redisCacheManager.setRedisManager(redisManager);
        return redisCacheManager;
    }


    @Bean
    public CustomAuthFilter customAuthFilter() {
        CustomAuthFilter authFilter = new CustomAuthFilter();
        authFilter.setAppCode(appConfig.getAppCode());
        return authFilter;
    }

    @Bean
    public PermsFilter permsFilter() {
        PermsFilter permsFilter = new PermsFilter();
        permsFilter.setAppCode(appConfig.getAppCode());
        return permsFilter;
    }

    @ConditionalOnProperty(name = "mycloud.sso.type", havingValue = "client", matchIfMissing = true)
    @Bean()
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
            DefaultWebSecurityManager securityManager,
            CustomAuthFilter authFilter, PermsFilter permsFilter,
            @Qualifier("ssoClientRealm") Realm realm) {
        securityManager.setRealm(realm);
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 添加自己的过滤器
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", authFilter);
        filterMap.put("perms", permsFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        //添加默认过滤权限
        Map<String, String> chainDefinitionMap = new LinkedHashMap<>(3);
        chainDefinitionMap.put("/favicon.ico", "anon");
        chainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(chainDefinitionMap);
        //登录
        shiroFilterFactoryBean.setLoginUrl(ssoUrl + "sso/login");
        //首页
        shiroFilterFactoryBean.setSuccessUrl(ssoUrl + "sso/index");
        //错误页面，认证不通过跳转
        shiroFilterFactoryBean.setUnauthorizedUrl(ssoUrl + "error/error");
        return shiroFilterFactoryBean;
    }


    //在thymeleaf中使用shiro标签
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

//    /**
//     * cookie对象;
//     *
//     * @return
//     */
//    public SimpleCookie rememberMeCookie() {
//        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
//        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//        //<!-- 记住我cookie生效时间7天 ,单位秒;-->
//        simpleCookie.setMaxAge(604800);
//        return simpleCookie;
//    }
//
//    /**
//     * cookie管理对象;记住我功能
//     *
//     * @return
//     */
//    @Bean
//    public CookieRememberMeManager rememberMeManager() {
//        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//        cookieRememberMeManager.setCookie(rememberMeCookie());
//        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
//        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3Kprsdag=="));
//        return cookieRememberMeManager;
//    }

    /**
     * 下面的代码是添加注解支持
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        // https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * shiro管理生命周期的东西,要静态？
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
