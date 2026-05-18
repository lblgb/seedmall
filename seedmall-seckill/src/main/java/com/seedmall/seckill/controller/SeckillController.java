/*
 * 秒杀接口控制器，提供大流量抢购入口。
 */
package com.seedmall.seckill.controller;

import com.seedmall.common.response.ApiResponse;
import com.seedmall.seckill.service.SeckillService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
