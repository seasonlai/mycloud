package com.season.movie.dao.config;

import com.season.movie.dao.base.BaseEnum;
import com.season.movie.dao.handler.AutoEnumTypeHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.awt.image.BufferedImage;

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

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource, Environment environment) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //配置信息在application.yml
//        bean.setTypeAliasesPackage(environment.getProperty("mybatis.type-aliases-package"));
        bean.setTypeAliasesPackage(typeAliasesPackage);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 添加XML目录
//        bean.setMapperLocations(resolver.getResources(environment.getProperty("mybatis.mapper-locations")));
        bean.setMapperLocations(resolver.getResources(mapperLocations));

        SqlSessionFactory sqlSessionFactory = bean.getObject();
        // 取得类型转换注册器
        TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration()
                .getTypeHandlerRegistry();
        // 注册默认枚举转换器
        typeHandlerRegistry.register(BaseEnum.class, AutoEnumTypeHandler.class);
        return sqlSessionFactory;

    }

}
