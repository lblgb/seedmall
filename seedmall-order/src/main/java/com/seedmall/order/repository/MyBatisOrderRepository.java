/*
 * MyBatis-Plus 订单仓储实现，负责订单表查询和写入。
 */
package com.seedmall.order.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.seedmall.order.entity.TradeOrder;
import com.seedmall.order.mapper.TradeOrderMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus 订单仓储实现。
 */
@Repository
public class MyBatisOrderRepository implements OrderRepository {

    private final TradeOrderMapper tradeOrderMapper;

    /**
     * 注入订单 Mapper。
     */
    public MyBatisOrderRepository(TradeOrderMapper tradeOrderMapper) {
        this.tradeOrderMapper = tradeOrderMapper;
    }

    /**
     * 按用户、商品和来源查询已有订单。
     */
    @Override
    public Optional<TradeOrder> findByBusinessKey(Long userId, Long productId, String source) {
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getUserId, userId)
                .eq(TradeOrder::getProductId, productId)
                .eq(TradeOrder::getSource, source)
                .last("LIMIT 1");
        return Optional.ofNullable(tradeOrderMapper.selectOne(wrapper));
    }

    /**
     * 插入订单记录。
     */
    @Override
    public void save(TradeOrder order) {
        tradeOrderMapper.insert(order);
    }

    /**
     * 仅将已创建状态的订单更新为已取消。
     */
    @Override
    public boolean cancelByBusinessKey(Long userId, Long productId, String source) {
        LambdaUpdateWrapper<TradeOrder> wrapper = new LambdaUpdateWrapper<TradeOrder>()
                .eq(TradeOrder::getUserId, userId)
                .eq(TradeOrder::getProductId, productId)
                .eq(TradeOrder::getSource, source)
                .eq(TradeOrder::getStatus, 0)
                .set(TradeOrder::getStatus, 2);
        return tradeOrderMapper.update(null, wrapper) > 0;
    }

    /**
     * 仅将已创建状态的订单更新为已支付。
     */
    @Override
    public boolean payByBusinessKey(Long userId, Long productId, String source) {
        LambdaUpdateWrapper<TradeOrder> wrapper = new LambdaUpdateWrapper<TradeOrder>()
                .eq(TradeOrder::getUserId, userId)
                .eq(TradeOrder::getProductId, productId)
                .eq(TradeOrder::getSource, source)
                .eq(TradeOrder::getStatus, 0)
                .set(TradeOrder::getStatus, 1);
        return tradeOrderMapper.update(null, wrapper) > 0;
    }

    /**
     * 仅将已取消状态的订单重新激活为已创建。
     */
    @Override
    public boolean reactivateCanceledByBusinessKey(Long userId, Long productId, String source, String orderNo, Integer quantity) {
        LambdaUpdateWrapper<TradeOrder> wrapper = new LambdaUpdateWrapper<TradeOrder>()
                .eq(TradeOrder::getUserId, userId)
                .eq(TradeOrder::getProductId, productId)
                .eq(TradeOrder::getSource, source)
                .eq(TradeOrder::getStatus, 2)
                .set(TradeOrder::getOrderNo, orderNo)
                .set(TradeOrder::getQuantity, quantity)
                .set(TradeOrder::getStatus, 0);
        return tradeOrderMapper.update(null, wrapper) > 0;
    }

    /**
     * 查询指定来源下超时未支付的订单。
     */
    @Override
    public List<TradeOrder> findCreatedBefore(String source, LocalDateTime cutoff, int limit) {
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getSource, source)
                .eq(TradeOrder::getStatus, 0)
                .lt(TradeOrder::getCreatedAt, cutoff)
                .last("LIMIT " + Math.max(1, limit));
        return tradeOrderMapper.selectList(wrapper);
    }
}
