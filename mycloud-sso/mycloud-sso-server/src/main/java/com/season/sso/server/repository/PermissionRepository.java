package com.season.sso.server.repository;

import com.season.sso.server.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/7/24.
 */
public interface PermissionRepository extends JpaSpecificationExecutor<Permission>, JpaRepository<Permission, Integer> {


    @Transactional
    @Modifying
    @Query("update Permission p set p.enable =:#{#obj.enable},p.description=:#{#obj.description}," +
            "p.permissionName=:#{#obj.permissionName},p.permissionUrl=:#{#obj.permissionUrl}," +
            "p.menu=:#{#obj.menu},p.permissionNeed=:#{#obj.permissionNeed},p.sort=:#{#obj.sort} where " +
            "p.permissionId = :#{#obj.permissionId}")
    void updateById(@Param("obj") Permission p);


    List<Permission> findByPermissionAppId(Integer appId);

    List<Permission> findByPermissionAppIdAndPermissionNeedLike(Integer appId, String permsLike);

    Set<Permission> findByPermissionIdIn(List<Integer> ids);

    List<Permission> findByPermissionAppIdOrderBySortDescCreateTimeDesc(Integer appId);

}
