package com.season.movie.dao.config;

import com.season.movie.dao.base.BaseEnum;
import com.season.movie.dao.handler.BaseEnumTypeHandler;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.util.Set;

/**
 * Created by season on 2018/8/19.
 */
@Configuration
@MapperScan("com.season.movie.dao.mapper")
public class Config {

    //
    @Value("${mybatis.type-aliases-package}")
    private String typeAliasesPackage;
    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;
    @Value("${mybatis.enum-package:}")
    private String enumPackage;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //配置信息在application.yml
        bean.setTypeAliasesPackage(typeAliasesPackage);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 添加XML目录
        bean.setMapperLocations(resolver.getResources(mapperLocations));

        SqlSessionFactory sqlSessionFactory = bean.getObject();

        if (!StringUtils.isEmpty(enumPackage)) {
            // 取得类型转换注册器
            TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration()
                    .getTypeHandlerRegistry();
            ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
            resolverUtil.find(new ResolverUtil.IsA(BaseEnum.class), enumPackage);
            Set<Class<? extends Class<?>>> handlerSet = resolverUtil.getClasses();
            for (Class<?> clazz : handlerSet) {
                // 注册自定义枚举转换器
                if (BaseEnum.class.isAssignableFrom(clazz) && !BaseEnum.class.equals(clazz)) {
                    typeHandlerRegistry.register(clazz, BaseEnumTypeHandler.class);
                }
            }
        }

        return sqlSessionFactory;

    }

}
