/*
 * 订单查询响应对象，用于前端展示异步下单结果。
 */
package com.seedmall.api.order;

/**
 * 订单查询响应。
 *
 * @param orderNo 订单号
 * @param userId 用户编号
 * @param productId 商品编号
 * @param quantity 购买数量
 * @param status 订单状态
 * @param source 订单来源
 */
public record OrderQueryResponse(String orderNo,
                                 Long userId,
                                 Long productId,
                                 Integer quantity,
                                 Integer status,
                                 String source) {
}
