/*
 * 订单创建消息监听器，承接秒杀服务削峰后的异步下单消息。
 */
package com.seedmall.order.listener;

import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单创建监听器。
 */
@Component
public class OrderCreateListener {

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
    @RabbitListener(queues = "seedmall.order.create")
    public void onMessage(CreateOrderRequest request) {
        orderService.create(request);
    }
}
