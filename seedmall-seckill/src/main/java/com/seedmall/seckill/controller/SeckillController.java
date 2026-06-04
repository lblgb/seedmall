/*
 * 秒杀接口控制器，提供大流量抢购入口。
 */
package com.seedmall.seckill.controller;

import com.seedmall.api.seckill.SeckillStockResponse;
import com.seedmall.api.seckill.SeckillCancelResponse;
import com.seedmall.common.response.ApiResponse;
import com.seedmall.seckill.service.SeckillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 秒杀接口。
 */
@RestController
public class SeckillController {

    private final SeckillService seckillService;

    /**
     * 注入秒杀服务。
     */
    public SeckillController(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    /**
     * 发起秒杀请求。
     */
    @PostMapping("/seckill/{productId}")
    public ApiResponse<String> reserve(@PathVariable Long productId, @RequestParam Long userId) {
        return ApiResponse.ok(seckillService.reserve(userId, productId));
    }

    /**
     * 查询秒杀 Redis 库存和用户排队状态。
     */
    @GetMapping("/seckill/{productId}/stock")
    public ApiResponse<SeckillStockResponse> queryStock(@PathVariable Long productId,
                                                        @RequestParam(required = false) Long userId) {
        return ApiResponse.ok(seckillService.queryStock(productId, userId));
    }

    /**
     * 初始化秒杀 Redis 库存。
     */
    @PostMapping("/seckill/{productId}/stock")
    public ApiResponse<SeckillStockResponse> initializeStock(@PathVariable Long productId, @RequestParam Integer stock) {
        return ApiResponse.ok(seckillService.initializeStock(productId, stock));
    }

    /**
     * 释放用户秒杀排队标记并回补 Redis 库存。
     */
    @PostMapping("/seckill/{productId}/cancel")
    public ApiResponse<SeckillStockResponse> releaseReservation(@PathVariable Long productId, @RequestParam Long userId) {
        return ApiResponse.ok(seckillService.releaseReservation(userId, productId));
    }

    /**
     * 统一取消秒杀订单并释放排队标记。
     */
    @PostMapping("/seckill/{productId}/order/cancel")
    public ApiResponse<SeckillCancelResponse> cancelSeckillOrder(@PathVariable Long productId, @RequestParam Long userId) {
        return ApiResponse.ok(seckillService.cancelSeckillOrder(userId, productId));
    }

    /**
     * 批量取消超时未支付的秒杀订单。
     */
    @PostMapping("/seckill/orders/timeout-cancel")
    public ApiResponse<List<SeckillCancelResponse>> cancelExpiredSeckillOrders(@RequestParam(defaultValue = "30") Integer timeoutMinutes,
                                                                               @RequestParam(defaultValue = "50") Integer limit) {
        return ApiResponse.ok(seckillService.cancelExpiredSeckillOrders(timeoutMinutes, limit));
    }
}
