/*
 * 订单事件响应对象，用于 Agent 诊断订单状态流转。
 */
package com.seedmall.api.order;

import java.time.LocalDateTime;

/**
 * 订单事件响应。
 *
 * @param orderNo 订单号
 * @param userId 用户编号
 * @param productId 商品编号
 * @param eventType 事件类型
 * @param detail 事件说明
 * @param createdAt 事件时间
 */
public record OrderEventResponse(String orderNo,
                                 Long userId,
                                 Long productId,
                                 String eventType,
                                 String detail,
                                 LocalDateTime createdAt) {
}
