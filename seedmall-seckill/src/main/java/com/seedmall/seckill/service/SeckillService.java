/*
 * 秒杀服务，演示 Redis 预扣库存与 MQ 削峰入口。
 */
package com.seedmall.seckill.service;

import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.common.exception.BizException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 秒杀业务服务。
 */
@Service
public class SeckillService {

    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 注入 Redis 与 MQ 模板。
     */
    public SeckillService(StringRedisTemplate redisTemplate, RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 执行秒杀预扣库存并发送异步下单消息。
     */
    public String reserve(Long userId, Long productId) {
        String stockKey = "seckill:stock:" + productId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock == null || stock < 0) {
            redisTemplate.opsForValue().increment(stockKey);
            throw new BizException(409, "秒杀库存不足");
        }
        CreateOrderRequest request = new CreateOrderRequest(userId, productId, 1, "SECKILL");
        rabbitTemplate.convertAndSend("seedmall.order.create", request);
        return "排队中";
    }
}
