package com.season.sso.client.jms;

import com.season.common.web.config.AppConfig;
import com.season.sso.client.service.ClientShiroService;
import com.season.sso.client.util.ShiroUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * Created by Administrator on 2018/8/14.
 */
@Service
public class SSOClientJMS {

    static Logger logger = LoggerFactory.getLogger(SSOClientJMS.class);

    @Autowired
    @Qualifier("shiroExecutor")
    ThreadPoolTaskExecutor executor;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ClientShiroService clientShiroService;

    public static final String UPDATE_APP= "shiro.update.app";
    public static final String UPDATE_USER= "shiro.update.user";
    public static final String LOGOUT_USER= "shiro.logout.user";


    // 使用JmsListener配置消费者监听的队列，其中msg是接收到的消息
    @JmsListener(destination = UPDATE_APP)
    public void updatePermission(String appCode) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("====线程：{}；updatePermission:{}", Thread.currentThread().getName(), appCode);
                if (!StringUtils.isEmpty(appCode) && appCode.equals(appConfig.getAppCode())) {
                    clientShiroService.updatePermission();
                }
            }
        });
    }

    @JmsListener(destination = UPDATE_USER)
    public void updateUser(String username) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("====线程：{}；updateUser:{}", Thread.currentThread().getName(), username);
                if (!StringUtils.isEmpty(username)) {
                    ShiroUtil.kickOutUser(username, false);
                }
            }
        });
    }

    @JmsListener(destination = LOGOUT_USER)
    public void logout(String appCode) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("====线程：{}；updatePermission:{}", Thread.currentThread().getName(), appCode);
                if (!StringUtils.isEmpty(appCode) && appCode.equals(appConfig.getAppCode())) {
                    clientShiroService.updatePermission();
                }
            }
        });

    }
}
