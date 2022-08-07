package com.rabbitmq.csl.service.Impl;

import com.rabbitmq.csl.Config.RabbitMQConfig;

import com.rabbitmq.csl.Config.RabbitMQConfirmCallback;
import com.rabbitmq.csl.service.MQService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public abstract class MQServiceImpl implements MQService {
    //日期格式化
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfirmCallback rabbitMQConfirmCallback;

    /**
     * 初始化rabbitMQConfirmCallback setReturnCallback到rabbitTemplate
     */
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(rabbitMQConfirmCallback);
        rabbitTemplate.setReturnCallback(rabbitMQConfirmCallback);
    }


    @Override
    public String sendMsg(String msg)  {
        try {
            Map<String, Object> map = getMap(msg);
            if ("hhhh".equals(msg)){
                map.put("msgId", "0453026647da4befb3d68f13fb7a2385");
            }
            rabbitTemplate.convertAndSend(RabbitMQConfig.RABBITMQ_DEMO_DIRECT_EXCHANGE, RabbitMQConfig.RABBITMQ_DEMO_DIRECT_ROUTING, map);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    public String sendfanoutMsg(String msg) {
        try {
            Map<String, Object> map = getMap(msg);
            rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE_DEMO_NAME,"", map);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    public String sendtopic(String msg,String routingKey) {
        try {
            Map<String, Object> map = getMap(msg);
            rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE_DEMO_NAME,routingKey, map);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    public String sendDelayMsg(String msg, Integer delayTime) {
        try {
            Map<String, Object> map = getMap(msg);
            rabbitTemplate.convertAndSend(RabbitMQConfig.DELAYED_EXCHANGE_NAME, RabbitMQConfig.DELAYED_ROUTING_KEY, map, a ->{
                a.getMessageProperties().setDelay(delayTime);
                return a;
            });
            return "ok";
        }catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    private Map<String, Object> getMap(String msg) {
        String msgId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        String sendTime = sdf.format(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put("msgId", msgId);
        map.put("sendTime", sendTime);
        map.put("msg", msg);
        return map;
    }
}
