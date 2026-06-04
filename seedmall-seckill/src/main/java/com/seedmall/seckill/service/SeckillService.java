/*
 * 秒杀服务，演示 Redis 预扣库存与 MQ 削峰入口。
 */
package com.seedmall.seckill.service;

import com.seedmall.api.mq.RocketMqTopics;
import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.api.order.OrderQueryResponse;
import com.seedmall.api.seckill.SeckillStockResponse;
import com.seedmall.common.exception.BizException;
import com.seedmall.seckill.integration.OrderStatusClient;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * 秒杀业务服务。
 */
@Service
public class SeckillService {

    private static final Duration RESERVATION_TTL = Duration.ofMinutes(30);
    private final StringRedisTemplate redisTemplate;
    private final RocketMQTemplate rocketMQTemplate;
    private final OrderStatusClient orderStatusClient;

    /**
     * 注入 Redis 与 MQ 模板。
     */
    public SeckillService(StringRedisTemplate redisTemplate, RocketMQTemplate rocketMQTemplate, OrderStatusClient orderStatusClient) {
        this.redisTemplate = redisTemplate;
        this.rocketMQTemplate = rocketMQTemplate;
        this.orderStatusClient = orderStatusClient;
    }

    /**
     * 执行秒杀预扣库存并发送异步下单消息。
     */
    public String reserve(Long userId, Long productId) {
        Optional<OrderQueryResponse> existingOrder = orderStatusClient.querySeckillOrder(userId, productId);
        if (existingOrder.isPresent() && Integer.valueOf(0).equals(existingOrder.get().status())) {
            return "已有待支付订单";
        }
        if (existingOrder.isPresent() && Integer.valueOf(1).equals(existingOrder.get().status())) {
            return "已有已支付订单";
        }

        String reservationKey = reservationKey(userId, productId);
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        Boolean reserved = operations.setIfAbsent(reservationKey, "1", RESERVATION_TTL);
        if (!Boolean.TRUE.equals(reserved)) {
            return "已在排队中";
        }

        String stockKey = stockKey(productId);
        Long stock = operations.decrement(stockKey);
        if (stock == null || stock < 0) {
            operations.increment(stockKey);
            redisTemplate.delete(reservationKey);
            throw new BizException(409, "秒杀库存不足");
        }
        CreateOrderRequest request = new CreateOrderRequest(userId, productId, 1, "SECKILL");
        rocketMQTemplate.convertAndSend(RocketMqTopics.ORDER_CREATE, request);
        return "排队中";
    }

    /**
     * 初始化指定商品的 Redis 秒杀库存。
     */
    public SeckillStockResponse initializeStock(Long productId, Integer stock) {
        if (stock == null || stock < 0) {
            throw new BizException(400, "秒杀库存不能小于 0");
        }
        redisTemplate.opsForValue().set(stockKey(productId), String.valueOf(stock));
        return new SeckillStockResponse(productId, stock, null, false, null);
    }

    /**
     * 查询指定商品的 Redis 秒杀库存和用户排队状态。
     */
    public SeckillStockResponse queryStock(Long productId, Long userId) {
        String stockValue = redisTemplate.opsForValue().get(stockKey(productId));
        Integer redisStock = stockValue == null ? null : Integer.valueOf(stockValue);
        if (userId == null) {
            return new SeckillStockResponse(productId, redisStock, null, false, null);
        }
        String reservationKey = reservationKey(userId, productId);
        boolean reserved = Boolean.TRUE.equals(redisTemplate.hasKey(reservationKey));
        Long ttlSeconds = reserved ? redisTemplate.getExpire(reservationKey) : null;
        return new SeckillStockResponse(productId, redisStock, userId, reserved, ttlSeconds);
    }

    /**
     * 释放用户排队标记，并在标记存在时回补 Redis 秒杀库存。
     */
    public SeckillStockResponse releaseReservation(Long userId, Long productId) {
        Optional<OrderQueryResponse> existingOrder = orderStatusClient.querySeckillOrder(userId, productId);
        if (existingOrder.isPresent() && !Integer.valueOf(2).equals(existingOrder.get().status())) {
            return queryStock(productId, userId);
        }

        String reservationKey = reservationKey(userId, productId);
        boolean reserved = Boolean.TRUE.equals(redisTemplate.hasKey(reservationKey));
        if (!reserved) {
            return queryStock(productId, userId);
        }
        redisTemplate.delete(reservationKey);
        Long redisStock = redisTemplate.opsForValue().increment(stockKey(productId));
        Integer stock = redisStock == null ? null : redisStock.intValue();
        return new SeckillStockResponse(productId, stock, userId, false, null);
    }

    /**
     * 构造商品库存 key。
     */
    private String stockKey(Long productId) {
        return "seckill:stock:" + productId;
    }

    /**
     * 构造用户抢购幂等 key。
     */
    private String reservationKey(Long userId, Long productId) {
        return "seckill:reservation:" + productId + ":" + userId;
    }
}
