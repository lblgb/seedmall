/*
 * 内容服务 MQ 配置，声明审核队列和 JSON 消息转换器。
 */
package com.seedmall.content.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 内容服务 MQ 配置。
 */
@Configuration
public class RabbitConfig {

    /**
     * 声明内容审核队列。
     */
    @Bean
    public Queue contentAuditQueue() {
        return new Queue("seedmall.content.audit", true);
    }

    /**
     * 使用 JSON 转换跨服务事件对象。
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
