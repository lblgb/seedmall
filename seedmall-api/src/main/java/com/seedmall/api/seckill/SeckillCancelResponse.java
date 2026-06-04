/*
 * 秒杀取消响应对象，聚合订单状态和 Redis 库存状态。
 */
package com.seedmall.api.seckill;

import com.seedmall.api.order.OrderQueryResponse;

/**
 * 秒杀取消响应。
 *
 * @param order 订单状态
 * @param stock 秒杀库存状态
 * @param reservationReleased 是否释放了排队标记
 */
public record SeckillCancelResponse(OrderQueryResponse order,
                                    SeckillStockResponse stock,
                                    boolean reservationReleased) {
}
