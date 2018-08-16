package com.season.sso.client.session;

import com.season.sso.client.constant.Constant;
import com.season.sso.client.jms.SSOClientJMS;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.crazycake.shiro.RedisSessionDAO;
import org.crazycake.shiro.exception.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/8/15.
 */
public class CustomSessionDao extends RedisSessionDAO {

    static Logger logger = LoggerFactory.getLogger(CustomSessionDao.class);

    @Value("${mycloud.sso.type:client}")
    private String ssoType;

    @Autowired
    private Constant constant;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public void update(Session session) throws UnknownSessionException {
        super.update(session);
        if (session.getId() != null) {
            String sessionId = session.getId().toString();
            //更新单点服务器token
            String token = redisTemplate.opsForValue().get(constant.getKeyPrefix() + constant.getMYCLOUD_SERVER_SESSION_ID() + sessionId);
            if (!Objects.isNull(token)) {
                redisTemplate.expire(constant.getKeyPrefix() + constant.getMYCLOUD_SERVER_SESSION_ID() + sessionId
                        , session.getTimeout(), TimeUnit.MILLISECONDS);
                redisTemplate.expire(constant.getKeyPrefix() + constant.getMYCLOUD_SERVER_CODE() + token
                        , session.getTimeout(), TimeUnit.MILLISECONDS);
            }
        } else {
            logger.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }
    }

    @Override
    public void delete(Session session) {
        super.delete(session);
        if (session != null && session.getId() != null) {
            String sessionId = session.getId().toString();
            //删除客户端所有redis缓存
            Boolean delete = redisTemplate.delete(constant.getKeyPrefix() + constant.getMYCLOUD_CLIENT_SESSION_ID() + sessionId);
            if (Objects.equals(delete, Boolean.FALSE)) {
                logger.warn("删除客户端键 {} 失败,不存在或出错", constant.getKeyPrefix() + constant.getMYCLOUD_CLIENT_SESSION_ID() + sessionId);
            }
            //删除服务端的缓存
            if ("server".equals(ssoType)) {
                String token = redisTemplate.opsForValue().get(constant.getKeyPrefix() + constant.getMYCLOUD_SERVER_SESSION_ID() + sessionId);
                delete = redisTemplate.delete(constant.getKeyPrefix() + constant.getMYCLOUD_SERVER_SESSION_ID() + sessionId);
                if (Objects.equals(delete, Boolean.FALSE)) {
                    logger.error("删除服务端键 {} 失败", constant.getKeyPrefix() + constant.getMYCLOUD_SERVER_SESSION_ID() + sessionId);
                }
                redisTemplate.delete(constant.getKeyPrefix() + constant.getMYCLOUD_SERVER_CODE() + token);
            }
        } else {
            logger.error("session or session id is null");
        }
    }

}
