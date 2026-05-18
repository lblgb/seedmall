/*
 * 创建订单请求对象。
 */
package com.seedmall.api.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 创建订单请求。
 *
 * @param userId 用户编号
 * @param productId 商品编号
 * @param quantity 购买数量
 * @param source 订单来源
 */
public record CreateOrderRequest(@NotNull(message = "用户编号不能为空") Long userId,
                                 @NotNull(message = "商品编号不能为空") Long productId,
                                 @Min(value = 1, message = "购买数量必须大于 0") Integer quantity,
                                 String source) {
}
