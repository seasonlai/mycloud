package com.season.sso.server.repository;

import com.season.sso.server.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by Administrator on 2018/7/20.
 */
@Repository
public interface AppRepository extends JpaRepository<App, Integer> {


    @Transactional
    @Modifying
    @Query("update App a set a.appName=:#{#app.appName}" +
            ",a.appCode=:#{#app.appCode},a.enable=:#{#app.enable} " +
            "where a.appId=:#{#app.appId}")
    int updateByAppId(@Param("app") App app);


    App findByAppCode(String appCode);

    @Transactional
    @Modifying
    @Query("update App a set a.enable=:status where a.appId in :ids")
    void updateEnableByIds(@Param("ids") Set<Integer> ids, @Param("status") byte enable);
}
