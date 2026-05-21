/*
 * 订单仓储接口，隔离业务服务与 MyBatis-Plus 细节。
 */
package com.seedmall.order.repository;

import com.seedmall.order.entity.TradeOrder;

import java.util.Optional;

/**
 * 订单仓储接口。
 */
public interface OrderRepository {

    /**
     * 按业务幂等键查询已有订单。
     */
    Optional<TradeOrder> findByBusinessKey(Long userId, Long productId, String source);

    /**
     * 保存订单。
     */
    void save(TradeOrder order);
}
