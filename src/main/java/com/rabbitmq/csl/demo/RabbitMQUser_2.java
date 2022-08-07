package com.rabbitmq.csl.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.csl.Config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: csl
 * @DateTime: 2022/6/25 11:46
 **/
@Component
@SuppressWarnings("ALL")
@RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.RABBITMQ_DEMO_TOPIC))
public class RabbitMQUser_2 {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RabbitHandler
    public void process(Map map, Channel channel, Message message) throws IOException {
        if (!"1".equals(stringRedisTemplate.opsForValue().get(map.get("msgId")))) {
            System.out.println(map.toString()+"user2");
            stringRedisTemplate.opsForValue().set((String) map.get("msgId"), "1",30, TimeUnit.MINUTES);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
