/*
 * 扣减商品库存请求对象。
 */
package com.seedmall.api.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 扣减商品库存请求。
 *
 * @param productId 商品编号
 * @param quantity 扣减数量
 */
public record DeductStockRequest(@NotNull(message = "商品编号不能为空") Long productId,
                                 @NotNull(message = "扣减数量不能为空")
                                 @Min(value = 1, message = "扣减数量必须大于 0") Integer quantity) {
}
