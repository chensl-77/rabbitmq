package com.rabbitmq.csl.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.csl.Config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
//@RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.DELAYED_QUEUE_NAME))
public class processDelayed {

//    @RabbitHandler
//    public void process(Map map){
//        System.out.println("processDelayed"+map);
//    }

    @RabbitListener(queues = RabbitMQConfig.DELAYED_QUEUE_NAME)
    public void receiveD(Message message, Channel channel) throws IOException {
        Map map = byteToObject(message.getBody(), Map.class);
        log.info("当前时间：{},延时队列收到消息：{}", new Date().toString(), map.get("msg"));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

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
