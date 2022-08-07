package com.rabbitmq.csl.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;


@Component
@Slf4j
public class RabbitMQConfirmCallback implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    /**
     * 监听消息是否到达Exchange
     * @param correlationData
     * @param success
     * @param s
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean success, String s) {
        if (success){
            System.out.println("消息投递成功");
        }else {
            System.out.println("消息投递失败");
        }

    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("消息没有路由到队列，获得返回的消息");
        System.out.println(byteToObject(message.getBody(), Map.class));
        System.out.println(s+"-"+s1+"-"+s2);
    }

//    /**
//     * 消息没有路由到队列，处理返回的消息
//     * @param message
//     */
//    @Override
//    public void returnedMessage(ReturnedMessage message) {
//        log.error("消息没有路由到队列，获得返回的消息");
//        log.info(message.toString());
//        System.out.println("123");
//    }



    /**
     * 字节转换（泛型）
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T byteToObject(byte[] bytes, Class<T> clazz) {
        T t;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            t = (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return t;
    }
}
