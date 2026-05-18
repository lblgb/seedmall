/*
 * 订单服务远程调用契约。
 */
package com.seedmall.api.order;

import com.seedmall.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}
