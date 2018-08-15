package com.season.movie.client.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    static Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired // 也可以注入JmsTemplate，JmsMessagingTemplate对JmsTemplate进行了封装  
    private JmsMessagingTemplate jmsTemplate;  
    // 发送消息，destination是发送到的队列，message是待发送的消息  
    public void sendMessage(String destination, final String message){
        jmsTemplate.convertAndSend(destination, message);

//        jmsTemplate.convertAndSend();
        logger.info("==thread_name: {}; 发送消息：{}",Thread.currentThread().getName(),message);
    }


}  