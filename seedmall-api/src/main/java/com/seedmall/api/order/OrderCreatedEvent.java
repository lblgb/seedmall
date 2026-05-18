/*
 * 订单创建事件对象，用于 MQ 异步流转。
 */
package com.seedmall.api.order;

import java.time.LocalDateTime;

/**
 * 订单创建事件。
 *
 * @param orderNo 订单号
 * @param userId 用户编号
 * @param productId 商品编号
 * @param createdAt 创建时间
 */
public record OrderCreatedEvent(String orderNo, Long userId, Long productId, LocalDateTime createdAt) {
}
