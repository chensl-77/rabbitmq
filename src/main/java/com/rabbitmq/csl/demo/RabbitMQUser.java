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

@Component
@SuppressWarnings("ALL")
@RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.RABBITMQ_DEMO_TOPIC))
public class RabbitMQUser {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RabbitHandler
    public void process(Map map, Channel channel, Message message) throws IOException, InterruptedException {
        if (!"1".equals(stringRedisTemplate.opsForValue().get(map.get("msgId")))) {
            System.out.println(map.toString()+"user1");
            stringRedisTemplate.opsForValue().set((String) map.get("msgId"), "1",30, TimeUnit.MINUTES);
        }

        //Redis分布式锁
//        if(stringRedisTemplate.opsForValue().setIfAbsent("key_prefix:msgId","threaId")){
//            //执行业务代码。。。
//            stringRedisTemplate.delete("key_prefix:msgId");//释放锁,
//            先判断再删(可利用lua脚本来保证原子性)
//        }
        //可以根据服务器的消费速度来负载均衡
        Thread.sleep(5000);//此处模拟服务器耗时较长场景
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
