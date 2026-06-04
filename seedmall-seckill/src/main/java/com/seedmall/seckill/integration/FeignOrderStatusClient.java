/*
 * Feign 订单状态客户端，负责从订单服务读取秒杀订单状态。
 */
package com.seedmall.seckill.integration;

import com.seedmall.api.order.OrderFeignClient;
import com.seedmall.api.order.OrderQueryResponse;
import com.seedmall.common.response.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Feign 订单状态客户端。
 */
@Component
public class FeignOrderStatusClient implements OrderStatusClient {

    private final OrderFeignClient orderFeignClient;

    /**
     * 注入订单服务 Feign 客户端。
     */
    public FeignOrderStatusClient(OrderFeignClient orderFeignClient) {
        this.orderFeignClient = orderFeignClient;
    }

    /**
     * 查询用户在指定商品上的秒杀订单。
     */
    @Override
    public Optional<OrderQueryResponse> querySeckillOrder(Long userId, Long productId) {
        ApiResponse<OrderQueryResponse> response = orderFeignClient.querySeckillOrder(userId, productId);
        return Optional.ofNullable(response.data());
    }
}
