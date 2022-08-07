package com.rabbitmq.csl.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.csl.Config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class processTopicC {

    public static int num = 0;

    enum Action {
        //处理成功
        SUCCESS,
        //可以重试的错误，消息重回队列
        RETRY,
        //无需重试的错误，拒绝消息，并从队列中删除
        REJECT
    }

    @RabbitHandler
    @RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_C))
    public void process(Map map, Message message, Channel channel){
        long tag = message.getMessageProperties().getDeliveryTag();
        Action action = Action.SUCCESS;
        try {
            System.out.println("消费者RabbitDemoConsumer从RabbitMQ服务端消费消息：" + map);
            if ("bad".equals(map.get("msg"))) {
                throw new IllegalArgumentException("测试：抛出可重回队列的异常");
            }
            if ("error".equals(map.get("msg"))) {
                throw new Exception("测试：抛出无需重回队列的异常");
            }
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            //根据异常的类型判断，设置action是可重试的，还是无需重试的
            action = Action.RETRY;
        } catch (Exception e2) {
            //打印异常
            e2.printStackTrace();
            //根据异常的类型判断，设置action是可重试的，还是无需重试的
            action = Action.REJECT;
        } finally {
            try {
                if (action == Action.SUCCESS) {
                    //multiple 表示是否批量处理。true表示批量ack处理小于tag的所有消息。false则处理当前消息
                    channel.basicAck(tag, false);
                } else if (action == Action.RETRY) {
                    //Nack，拒绝策略，消息重回队列
                    if (num++ > 5){
                        channel.basicNack(tag, false, false);
                    }else {
                        channel.basicNack(tag, false, true);
                    }
                } else {
                    //Nack，拒绝策略，并且从队列中删除,进入死信队列
                   channel.basicNack(tag, false, false);
//                    channel.basicReject(1,false);
                }
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }}
}
