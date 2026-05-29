/*
 * 订单服务远程调用契约。
 */
package com.seedmall.api.order;

import com.seedmall.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 订单服务 Feign 客户端。
 */
@FeignClient(name = "seedmall-order", path = "/orders")
public interface OrderFeignClient {

    /**
     * 创建订单。
     */
    @PostMapping
    ApiResponse<String> create(@Valid @RequestBody CreateOrderRequest request);

    /**
     * 查询用户指定商品的秒杀订单。
     */
    @GetMapping("/seckill")
    ApiResponse<OrderQueryResponse> querySeckillOrder(@RequestParam Long userId, @RequestParam Long productId);
}
