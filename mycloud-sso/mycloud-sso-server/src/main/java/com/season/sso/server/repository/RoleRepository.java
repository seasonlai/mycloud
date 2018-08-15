package com.season.sso.server.repository;

import com.season.sso.server.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/7/24.
 */
@Repository
public interface RoleRepository extends JpaSpecificationExecutor<Role>, JpaRepository<Role, Integer> {

    List<Role> findByRoleAppId(Integer appId);

    Set<Role> findByRoleAppIdAndRoleIdIn(Integer appId, List<Integer> roleIds);

    @Transactional
    @Modifying
    @Query("update Role r set r.enable=:status where r.roleId in :ids")
    void updateEnable(@Param("ids") Set<Integer> ids, @Param("status") byte status);

    @Transactional
    @Modifying
    @Query("update Role r set r.sort=:#{#obj.sort},r.description=:#{#obj.description}," +
            "r.roleName=:#{#obj.roleName}, r.enable=:#{#obj.enable} where r.roleId = :#{#obj.roleId}")
    void updateByRoleId(@Param("obj") Role role);
}
