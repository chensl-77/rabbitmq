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
@RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.LIND_TOPIC_EXCHANGE_QUEUE))
public class processSixin {

    /**
     * 死信队列
     * @param map
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void processSixin(Map map, Channel channel,Message message) throws IOException {
        System.out.println("processSixin"+map.toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
