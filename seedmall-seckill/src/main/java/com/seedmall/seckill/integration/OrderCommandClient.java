/*
 * 订单命令客户端接口，隔离秒杀编排与订单写操作远程调用细节。
 */
package com.seedmall.seckill.integration;

import com.seedmall.api.order.OrderQueryResponse;

import java.util.List;
import java.util.Optional;

/**
 * 订单命令客户端。
 */
public interface OrderCommandClient {

    /**
     * 取消用户在指定商品上的秒杀订单。
     */
    Optional<OrderQueryResponse> cancelSeckillOrder(Long userId, Long productId);

    /**
     * 批量取消超时未支付的秒杀订单。
     */
    List<OrderQueryResponse> cancelExpiredSeckillOrders(Integer timeoutMinutes, Integer limit);
}
