/*
 * 秒杀服务，演示 Redis 预扣库存与 MQ 削峰入口。
 */
package com.seedmall.seckill.service;

import com.seedmall.api.mq.RocketMqTopics;
import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.common.exception.BizException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 秒杀业务服务。
 */
@Service
public class SeckillService {

    private static final Duration RESERVATION_TTL = Duration.ofMinutes(30);
    private final StringRedisTemplate redisTemplate;
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 注入 Redis 与 MQ 模板。
     */
    public SeckillService(StringRedisTemplate redisTemplate, RocketMQTemplate rocketMQTemplate) {
        this.redisTemplate = redisTemplate;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    /**
     * 执行秒杀预扣库存并发送异步下单消息。
     */
    public String reserve(Long userId, Long productId) {
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
