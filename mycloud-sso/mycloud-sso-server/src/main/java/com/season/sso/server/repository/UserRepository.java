package com.season.sso.server.repository;

import com.season.sso.server.entity.Role;
import com.season.sso.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/6/1.
 */
public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    User findByUserName(String name);

    @Transactional
    @Modifying
    @Query("update User u set u.locked=:status where u.userId in :ids")
    void updateLockByIds(@Param("ids") Set<Long> ids, @Param("status") byte lockStatus);

    @Transactional
    @Modifying
    @Query("update User u set u.password=:#{#user.password} where u.userId=:#{#user.userId}")
    void updatePasswordById(@Param("user") User user);


    @Transactional
    @Modifying
    @Query("update User u set u.lastIp=:#{#user.lastIp},u.lastVisit=:#{#user.lastVisit} where u.userId=:#{#user.userId}")
    void updateIpAndVistById(@Param("user") User user);

    Set<User> findByRolesIn(List<Role> roles);
    Set<User> findByRolesIn(Set<Role> roles);

}
