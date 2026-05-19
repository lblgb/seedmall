/*
 * 订单创建消息监听器，承接秒杀服务削峰后的异步下单消息。
 */
package com.seedmall.order.listener;

import com.seedmall.api.mq.RocketMqTopics;
import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.order.service.OrderService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 订单创建监听器。
 */
@Component
@RocketMQMessageListener(topic = RocketMqTopics.ORDER_CREATE, consumerGroup = "seedmall-order-create-consumer")
public class OrderCreateListener implements RocketMQListener<CreateOrderRequest> {

    private final OrderService orderService;

    /**
     * 注入订单服务。
     */
    public OrderCreateListener(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 消费异步下单消息。
     */
    @Override
    public void onMessage(CreateOrderRequest request) {
        orderService.create(request);
    }
}
