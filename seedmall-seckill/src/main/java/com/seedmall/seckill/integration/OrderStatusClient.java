/*
 * 订单状态客户端接口，隔离秒杀服务与订单远程调用细节。
 */
package com.seedmall.seckill.integration;

import com.seedmall.api.order.OrderQueryResponse;

import java.util.Optional;

/**
 * 订单状态客户端。
 */
public interface OrderStatusClient {

    /**
     * 查询用户在指定商品上的秒杀订单。
     */
    Optional<OrderQueryResponse> querySeckillOrder(Long userId, Long productId);
}
