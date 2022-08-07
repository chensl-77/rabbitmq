package com.rabbitmq.csl.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Component
@Configuration
public class DirectRabbitConfig implements BeanPostProcessor {

    //这是创建交换机和队列用的rabbitAdmin对象
    @Resource
    private RabbitAdmin rabbitAdmin;

    //初始化rabbitAdmin对象
    @Bean
    @SuppressWarnings("All")
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 只有设置为 true，spring 才会加载 RabbitAdmin 这个类
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    //实例化bean后，也就是Bean的后置处理器
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //创建交换机
        rabbitAdmin.declareExchange(rabbitmqDemoDirectExchange());
        //创建队列
        rabbitAdmin.declareQueue(rabbitmqDemoDirectQueue());
        rabbitAdmin.declareExchange(rabbitmqDemoFanoutExchange());
        rabbitAdmin.declareQueue(fanoutExchangeQueueB());
        rabbitAdmin.declareQueue(fanoutExchangeQueueA());
        return null;
    }


    @Bean
    public Queue rabbitmqDemoDirectQueue() {
        /**
         * 1、name:    队列名称
         * 2、durable: 是否持久化
         * 3、exclusive: 是否独享、排外的。如果设置为true，定义为排他队列。则只有创建者可以使用此队列。也就是private私有的。
         * 4、autoDelete: 是否自动删除。也就是临时队列。当最后一个消费者断开连接后，会自动删除。
         * */
        return new Queue(RabbitMQConfig.RABBITMQ_DEMO_TOPIC, true, false, false);
    }

    @Bean
    public DirectExchange rabbitmqDemoDirectExchange() {
        //Direct交换机
        return new DirectExchange(RabbitMQConfig.RABBITMQ_DEMO_DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public Binding bindDirect() {
        //链式写法，绑定交换机和队列，并设置匹配键
        return BindingBuilder
                //绑定队列
                .bind(rabbitmqDemoDirectQueue())
                //到交换机
                .to(rabbitmqDemoDirectExchange())
                //并设置匹配键
                .with(RabbitMQConfig.RABBITMQ_DEMO_DIRECT_ROUTING);
    }


    @Bean
    public Queue fanoutExchangeQueueA(){
        return new Queue(RabbitMQConfig.FANOUT_EXCHANGE_QUEUE_TOPIC_A,true,false,false);
    }


    @Bean
    public Queue fanoutExchangeQueueB(){
        return new Queue(RabbitMQConfig.FANOUT_EXCHANGE_QUEUE_TOPIC_B,true,false,false);
    }

    @Bean
    public FanoutExchange rabbitmqDemoFanoutExchange() {
        //创建FanoutExchange类型交换机
        return new FanoutExchange(RabbitMQConfig.FANOUT_EXCHANGE_DEMO_NAME, true, false);
    }

    @Bean
    public Binding bindFanoutA() {
        //队列A绑定到FanoutExchange交换机
        return BindingBuilder.bind(fanoutExchangeQueueA()).to(rabbitmqDemoFanoutExchange());
    }

    @Bean
    public Binding bindFanoutB() {
        //队列B绑定到FanoutExchange交换机
        return BindingBuilder.bind(fanoutExchangeQueueB()).to(rabbitmqDemoFanoutExchange());
    }

    @Bean
    public TopicExchange rabbitmqDemoTopicExchange() {
        //配置TopicExchange交换机
        return new TopicExchange(RabbitMQConfig.TOPIC_EXCHANGE_DEMO_NAME, true, false);
    }

    @Bean
    public Queue topicExchangeQueueA() {
        //创建队列1
        return new Queue(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_A, true, false, false);
    }

    @Bean
    public Queue topicExchangeQueueB() {
        //创建队列2
        return new Queue(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_B, true, false, false);
    }

    @Bean
    public Queue topicExchangeQueueC() {
//        Map map = new HashMap();
//        map.put("x-dead-letter-exchange", RabbitMQConfig.LIND_DL_EXCHANGE);
//        map.put("x-message-ttl", 10000);
//        map.put("x-dead-letter-routing-key", "sixin.queue");
//        //创建队列3
//        return new Queue(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_C, true, false, false,map);
//        return new Queue(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_C, true, false, false);
        return QueueBuilder.durable(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_C)
                .withArgument("x-dead-letter-exchange", RabbitMQConfig.LIND_DL_EXCHANGE)//设置死信交换机
//              .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-routing-key", "sixin.queue")//设置死信routingKey
                .build();
    }


    @Bean
    public Binding bindTopicA() {
        //队列A绑定到topicExchange交换机
        return BindingBuilder.bind(topicExchangeQueueA())
                .to(rabbitmqDemoTopicExchange())
                .with("a.*");
    }

    @Bean
    public Binding bindTopicB() {
        //队列B绑定到topicExchange交换机
        return BindingBuilder.bind(topicExchangeQueueB())
                .to(rabbitmqDemoTopicExchange())
                .with("b.*");
    }

    @Bean
    public Binding bindTopicC() {
        //队列C绑定到topicExchange交换机
        return BindingBuilder.bind(topicExchangeQueueC())
                .to(rabbitmqDemoTopicExchange())
                .with("b.#");
    }

    @Bean
    public Queue lindtopicExchangeQueue() {
        //创建死信队列
        return new Queue(RabbitMQConfig.LIND_TOPIC_EXCHANGE_QUEUE, true, false, false);
    }
    /**
     * 死信交换机
     * @return
     */
    @Bean
    public TopicExchange lindtopicExchange() {
        //创建死信交换机 同一个项目的死信交换机可以共用一个，然后为每个业务队列分配一个单独的路由key。
        return (TopicExchange) ExchangeBuilder.topicExchange(RabbitMQConfig.LIND_DL_EXCHANGE).durable(true)
                .build();
    }

    @Bean
    public Binding bindLindTopic(){
        return BindingBuilder.bind(lindtopicExchangeQueue())
                .to(lindtopicExchange())
                .with("sixin.queue");
    }



    @Bean
    public Queue immediateQueue(){
        return new Queue(RabbitMQConfig.DELAYED_QUEUE_NAME);
    }

    @Bean
    public CustomExchange customExchange(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(RabbitMQConfig.DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding bindingNotify() {
        return BindingBuilder.bind(immediateQueue()).to(customExchange()).with(RabbitMQConfig.DELAYED_ROUTING_KEY).noargs();
    }

}
