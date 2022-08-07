package com.rabbitmq.csl.service;

public interface MQService {

    String sendMsg(String msg);

    String sendfanoutMsg(String msg);

    String sendtopic(String msg,String routingKey);

    String sendDelayMsg(String msg, Integer delayTime);
}
