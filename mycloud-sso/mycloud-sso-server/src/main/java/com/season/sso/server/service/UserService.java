package com.season.sso.server.service;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;
import com.season.sso.client.model.LoginUser;
import com.season.sso.client.util.ShiroUtil;
import com.season.sso.client.constant.Constant;
import com.season.sso.server.entity.Role;
import com.season.sso.server.entity.User;
import com.season.sso.server.jms.SSOServerJMS;
import com.season.sso.server.repository.UserRepository;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Administrator on 2018/6/1.
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    SSOServerJMS ssoServerJMS;

    public User findById(Long userId) {
        try {

            return userRepository.findById(userId).get();
        } catch (Throwable t) {
            throw new BaseException(ResultCode.SERVICE_ERROR, "查询用户失败", t);
        }
    }

    public User findByUserName(String name) {
        return userRepository.findByUserName(name);
    }

    public boolean exist(String name) {
//        User u = new User();
//        u.setUserName(name);
        return userRepository.findByUserName(name) != null;
    }

    public Set<Role> getUserRoles(Long userId) {
        if (Objects.isNull(userId)) {
            return Collections.emptySet();
        }
        try {
            User user = userRepository.findById(userId).get();
            return user.getRoles();
        } catch (Throwable t) {
            throw new BaseException(ResultCode.DAO_ERROR, "获取用户角色失败", t);
        }
    }

    public User register(String name, String pwd) {
        User user = new User();
        user.setUserName(name);
        user.setPassword(pwd);
        return register(user);
    }

    public User register(User user) {
        if (StringUtils.isEmpty(user.getUserName()) || StringUtils.isEmpty(user.getPassword())) {
            throw new BaseException("用户名或密码为空");
        }
        //对密码进行加密
        SimpleHash simpleHash = new SimpleHash(Constant.HASH_ALGORITHM, user.getPassword());
        simpleHash.setIterations(Constant.HASH_ITERATION);
        user.setPassword(simpleHash.toHex());
        User u = userRepository.save(user);
        return u;
    }

    public static void main(String... args) {
        SimpleHash simpleHash = new SimpleHash(Constant.HASH_ALGORITHM, "123456");
        simpleHash.setIterations(Constant.HASH_ITERATION);
        System.out.println(simpleHash);
    }

    public Page<User> findAdminByPage(int pageNum, int pageSize, String userName) {
        PageRequest pageParam = PageRequest.of(pageNum, pageSize);
        Specification<User> spec = new Specification<User>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder cb) {
                Path<Integer> type = root.get("userType");
                Predicate p = cb.equal(type, 1);
                if (!StringUtils.isEmpty(userName)) {
                    Path<String> name = root.get("userName");
                    Predicate like = cb.like(name, userName);
                    p = cb.and(p, like);
                }
                return p;
            }
        };
        return userRepository.findAll(spec, pageParam);
    }

    public void lockOrUnlock(List<User> users, int lockStatus) {
        if (lockStatus < 0 || lockStatus > 1) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, "锁定参数不合法");
        }
        Set<Long> ids = new HashSet<>();
        for (User user : users) {
            if (user.getLocked() != lockStatus) {
                ids.add(user.getUserId());
            }
        }
        if (!CollectionUtils.isEmpty(ids))
            userRepository.updateLockByIds(ids, (byte) lockStatus);

        //让锁住的用户重新认证
        if (lockStatus == 1) {
            for (User user : users) {
                ssoServerJMS.updateUser(user.getUserName());
            }
        }
    }

    public void edit(User user, Integer option) {
        if (Objects.isNull(user) || Objects.isNull(option)) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, "参数为空");
        }
        switch (option) {
            case 1:
                register(user);
                break;

            case 2:
                userRepository.updatePasswordById(user);
                break;
            default:
                throw new BaseException(ResultCode.VALIDATE_ERROR, "参数不合法");

        }

    }

    @Transactional
    public void del(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            return;
        }
        Subject subject = SecurityUtils.getSubject();
        LoginUser loginUser = (LoginUser) subject.getPrincipal();
        for (User user : users) {
            if (Objects.equals(user.getUserId(), loginUser.getId())) {
                throw new BaseException(ResultCode.SERVICE_ERROR, "用户不能删除自己");
            }
        }
        userRepository.deleteInBatch(users);
    }

    public void loginSuccess(LoginUser user, HttpServletRequest request) {
        User u = new User();
        u.setUserId(user.getId());
        u.setLastIp(request.getRemoteHost());
        u.setLastVisit(new Date());
        userRepository.updateIpAndVistById(u);
    }
}
