/*
 * 订单接口控制器，提供订单创建入口。
 */
package com.seedmall.order.controller;

import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.api.order.OrderQueryResponse;
import com.seedmall.common.response.ApiResponse;
import com.seedmall.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单接口。
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 注入订单服务。
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单。
     */
    @PostMapping
    public ApiResponse<String> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok(orderService.create(request));
    }

    /**
     * 查询用户指定商品的秒杀订单。
     */
    @GetMapping("/seckill")
    public ApiResponse<OrderQueryResponse> querySeckillOrder(@RequestParam Long userId, @RequestParam Long productId) {
        return ApiResponse.ok(orderService.querySeckillOrder(userId, productId).orElse(null));
    }
}
