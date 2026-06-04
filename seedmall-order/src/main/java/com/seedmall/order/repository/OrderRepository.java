/*
 * 订单仓储接口，隔离业务服务与 MyBatis-Plus 细节。
 */
package com.seedmall.order.repository;

import com.seedmall.order.entity.TradeOrder;

import java.time.LocalDateTime;
import java.util.List;
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

    /**
     * 取消已创建的业务订单。
     */
    boolean cancelByBusinessKey(Long userId, Long productId, String source);

    /**
     * 支付已创建的业务订单。
     */
    boolean payByBusinessKey(Long userId, Long productId, String source);

    /**
     * 重新激活已取消的业务订单。
     */
    boolean reactivateCanceledByBusinessKey(Long userId, Long productId, String source, String orderNo, Integer quantity);

    /**
     * 查询指定来源下超时未支付的订单。
     */
    List<TradeOrder> findCreatedBefore(String source, LocalDateTime cutoff, int limit);
}
