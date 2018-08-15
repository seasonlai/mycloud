package com.season.sso.client.session;

import com.season.sso.client.jms.SSOClientJMS;
import org.apache.shiro.session.Session;
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

/**
 * Created by Administrator on 2018/8/15.
 */
public class CustomSessionDao extends RedisSessionDAO {

    static Logger logger = LoggerFactory.getLogger(CustomSessionDao.class);

    @Value("${mycloud.sso.type:client}")
    private String ssoType;
    @Value("${app.cache.redis.keyPrefix:mycloud:cache:}")
    private String keyPrefix;
    @Value("${sso.server.idPrefix:server-session-id:}")
    private String MYCLOUD_SERVER_SESSION_ID;
    @Value("${sso.server.idsPrefix:server-session-ids:}")
    private String MYCLOUD_SERVER_SESSION_IDS;
    @Value("${sso.server.code:server-code:}")
    private String MYCLOUD_SERVER_CODE;
    @Value("${sso.client.idPrefix:client-session-id:}")
    private String MYCLOUD_CLIENT_SESSION_ID;
    @Value("${sso.client.idsPrefix:client-session-ids:}")
    private String MYCLOUD_CLIENT_SESSION_IDS;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void delete(Session session) {
        super.delete(session);
        if (session != null && session.getId() != null) {
            String sessionId = session.getId().toString();
            //删除客户端所有redis缓存
            Boolean delete = redisTemplate.delete(keyPrefix + MYCLOUD_CLIENT_SESSION_ID + sessionId);
            if (Objects.equals(delete, Boolean.FALSE)) {
                logger.warn("删除客户端键 {} 失败,不存在或出错", keyPrefix + MYCLOUD_CLIENT_SESSION_ID + sessionId);
            }
            //删除服务端的缓存
            if ("server".equals(ssoType)) {
                String token = redisTemplate.opsForValue().get(keyPrefix + MYCLOUD_SERVER_SESSION_ID + sessionId);
                delete = redisTemplate.delete(keyPrefix + MYCLOUD_SERVER_SESSION_ID + sessionId);
                if (Objects.equals(delete, Boolean.FALSE)) {
                    logger.error("删除服务端键 {} 失败", keyPrefix + MYCLOUD_SERVER_SESSION_ID + sessionId);
                }
                redisTemplate.delete(keyPrefix + MYCLOUD_SERVER_SESSION_IDS + sessionId);
                redisTemplate.delete(keyPrefix + MYCLOUD_SERVER_CODE + token);
            }
        } else {
            logger.error("session or session id is null");
        }
    }

}
