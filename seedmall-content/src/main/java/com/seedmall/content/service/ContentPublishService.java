/*
 * 内容发布服务，封装发布、审核事件发送和缓存扩展点。
 */
package com.seedmall.content.service;

import com.seedmall.api.content.ContentAuditEvent;
import com.seedmall.content.entity.SeedContent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内容发布服务。
 */
@Service
public class ContentPublishService {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1000);
    private final RabbitTemplate rabbitTemplate;

    /**
     * 注入 MQ 模板。
     */
    public ContentPublishService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发布内容并发送审核事件。
     */
    public SeedContent publish(SeedContent content) {
        content.setId(ID_GENERATOR.incrementAndGet());
        content.setAuditStatus(0);
        ContentAuditEvent event = new ContentAuditEvent(
                content.getId(),
                content.getAuthorId(),
                content.getBody(),
                LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend("seedmall.content.audit", event);
        return content;
    }
}
