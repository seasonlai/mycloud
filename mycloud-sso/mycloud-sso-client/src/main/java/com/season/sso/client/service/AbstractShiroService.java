package com.season.sso.client.service;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;
import com.season.common.web.util.SpringUtil;
import com.season.sso.client.util.ShiroUtil;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.servlet.Filter;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/10.
 */
public abstract class AbstractShiroService {


    protected  ShiroFilterFactoryBean getShiroFilterFactoryBean(){
        return SpringUtil.getBean(ShiroFilterFactoryBean.class);
    }

    protected abstract Map<String, String> loadFilterChainDefinitions();

    /**
     * 重新加载权限
     */
    public void updatePermission() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = getShiroFilterFactoryBean();
        if (Objects.isNull(shiroFilterFactoryBean)) {
            throw new RuntimeException("shiroFilterFactoryBean不能为空");
        }
        Map<String, String> newFilterChainMap = loadFilterChainDefinitions();
        if (Objects.isNull(newFilterChainMap)) {
            return;
        }
        synchronized (shiroFilterFactoryBean) {
            AbstractShiroFilter shiroFilter = null;
            try {
                shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean
                        .getObject();
            } catch (Exception e) {
                throw new RuntimeException(
                        "从shiroFilterFactoryBean获取ShiroFilter失败!");
            }

            PathMatchingFilterChainResolver filterChainResolver =
                    (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();
            DefaultFilterChainManager manager =
                    (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
            //清掉每个filter里的path
            Map<String, Filter> filters = manager.getFilters();
            if (!CollectionUtils.isEmpty(filters)) {
                for (Filter filter : filters.values()) {
                    if (filter instanceof PathMatchingFilter) {
                        try {
                            Field appliedPathsField = ReflectionUtils
                                    .findField(PathMatchingFilter.class, "appliedPaths");
                            ReflectionUtils.makeAccessible(appliedPathsField);
                            LinkedHashMap appliedPaths = (LinkedHashMap) ReflectionUtils.getField(appliedPathsField, filter);
                            appliedPaths.clear();
                        } catch (Throwable t) {
                            throw new RuntimeException("反射清理权限时失败", t);
                        }
                    }
                }
            }
            // 清空 URL->filter 的匹配
            manager.getFilterChains().clear();
            //清空 URL->配置 的匹配
            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            shiroFilterFactoryBean
                    .setFilterChainDefinitionMap(newFilterChainMap);
            // 重新构建生成
            Map<String, String> chains = shiroFilterFactoryBean
                    .getFilterChainDefinitionMap();

            //重建filterChains
            for (Map.Entry<String, String> entry : chains.entrySet()) {
                String url = entry.getKey();
                String chainDefinition = entry.getValue().trim()
                        .replace(" ", "");
                manager.createChain(url, chainDefinition);
            }
            ShiroUtil.kickOutAllUser(false);
        }
    }
}
