package com.season.sso.server.jms;

import com.season.sso.client.jms.SSOClientJMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by Administrator on 2018/8/14.
 */
@Service
public class SSOServerJMS {

    @Autowired
    private JmsTemplate jmsTemplate;

    static Logger logger = LoggerFactory.getLogger(SSOServerJMS.class);

    public void updateApp(String appCode) {
        if (StringUtils.isEmpty(appCode)) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("线程：{}  执行updateApp: {}", Thread.currentThread().getName(), appCode);
        }
        jmsTemplate.convertAndSend(SSOClientJMS.UPDATE_APP, appCode);
    }

    public void updateUser(String username) {
        if (StringUtils.isEmpty(username)) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("线程：{}  执行updateUser: {}", Thread.currentThread().getName(), username);
        }
        jmsTemplate.convertAndSend(SSOClientJMS.UPDATE_USER, username);
    }
}
