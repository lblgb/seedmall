/*
 * RocketMQ 主题常量测试，约束跨服务消息 topic 名称不漂移。
 */
package com.seedmall.api.mq;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RocketMQ 主题常量测试。
 */
class RocketMqTopicsTest {

    /**
     * 校验内容审核和订单创建主题名称。
     */
    @Test
    void shouldExposeStableTopicNames() {
        assertThat(RocketMqTopics.CONTENT_AUDIT).isEqualTo("seedmall-content-audit-topic");
        assertThat(RocketMqTopics.ORDER_CREATE).isEqualTo("seedmall-order-create-topic");
    }
}
