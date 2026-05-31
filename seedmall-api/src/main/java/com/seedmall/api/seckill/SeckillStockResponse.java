/*
 * 秒杀库存响应对象，用于观察 Redis 预扣库存和用户排队状态。
 */
package com.seedmall.api.seckill;

/**
 * 秒杀库存响应。
 *
 * @param productId 商品编号
 * @param redisStock Redis 秒杀库存
 * @param userId 用户编号
 * @param reserved 用户是否已有排队标记
 * @param reservationTtlSeconds 排队标记剩余秒数
 */
public record SeckillStockResponse(Long productId,
                                   Integer redisStock,
                                   Long userId,
                                   boolean reserved,
                                   Long reservationTtlSeconds) {
}
