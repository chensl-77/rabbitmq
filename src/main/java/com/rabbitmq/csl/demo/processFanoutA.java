package com.rabbitmq.csl.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.csl.Config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.FANOUT_EXCHANGE_QUEUE_TOPIC_A))
public class processFanoutA {


    @RabbitHandler
    public void processFanoutA(Map map, Channel channel,Message message) throws IOException {
        System.out.println("processFanoutA"+map.toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
