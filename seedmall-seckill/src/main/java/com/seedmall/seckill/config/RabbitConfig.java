/*
 * 秒杀服务 MQ 配置，声明异步下单队列和 JSON 消息转换器。
 */
package com.seedmall.seckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀服务 MQ 配置。
 */
@Configuration
public class RabbitConfig {

    /**
     * 声明异步下单队列。
     */
    @Bean
    public Queue orderCreateQueue() {
        return new Queue("seedmall.order.create", true);
    }

    /**
     * 使用 JSON 转换异步下单消息。
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
