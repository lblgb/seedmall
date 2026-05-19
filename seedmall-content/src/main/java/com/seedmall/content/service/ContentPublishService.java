/*
 * 内容发布服务，封装发布、审核事件发送和缓存扩展点。
 */
package com.seedmall.content.service;

import com.seedmall.api.content.ContentAuditEvent;
import com.seedmall.api.mq.RocketMqTopics;
import com.seedmall.content.entity.SeedContent;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内容发布服务。
 */
@Service
public class ContentPublishService {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1000);
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 注入 MQ 模板。
     */
    public ContentPublishService(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
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
        rocketMQTemplate.convertAndSend(RocketMqTopics.CONTENT_AUDIT, event);
        return content;
    }
}
