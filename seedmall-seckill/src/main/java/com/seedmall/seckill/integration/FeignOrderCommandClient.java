/*
 * Feign 订单命令客户端，负责调用订单服务的写操作。
 */
package com.seedmall.seckill.integration;

import com.seedmall.api.order.OrderFeignClient;
import com.seedmall.api.order.OrderQueryResponse;
import com.seedmall.common.response.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Feign 订单命令客户端。
 */
@Component
public class FeignOrderCommandClient implements OrderCommandClient {

    private final OrderFeignClient orderFeignClient;

    /**
     * 注入订单服务 Feign 客户端。
     */
    public FeignOrderCommandClient(OrderFeignClient orderFeignClient) {
        this.orderFeignClient = orderFeignClient;
    }

    /**
     * 取消用户在指定商品上的秒杀订单。
     */
    @Override
    public Optional<OrderQueryResponse> cancelSeckillOrder(Long userId, Long productId) {
        ApiResponse<OrderQueryResponse> response = orderFeignClient.cancelSeckillOrder(userId, productId);
        return Optional.ofNullable(response.data());
    }

    /**
     * 批量取消超时未支付的秒杀订单。
     */
    @Override
    public List<OrderQueryResponse> cancelExpiredSeckillOrders(Integer timeoutMinutes, Integer limit) {
        ApiResponse<List<OrderQueryResponse>> response = orderFeignClient.cancelExpiredSeckillOrders(timeoutMinutes, limit);
        return response.data() == null ? List.of() : response.data();
    }
}
