/*
 * RocketMQ 主题常量，集中约束跨服务消息主题名称。
 */
package com.seedmall.api.mq;

/**
 * RocketMQ 主题常量。
 */
public final class RocketMqTopics {

    public static final String CONTENT_AUDIT = "seedmall-content-audit-topic";
    public static final String ORDER_CREATE = "seedmall-order-create-topic";

    /**
     * 禁止实例化常量类。
     */
    private RocketMqTopics() {
    }
}
