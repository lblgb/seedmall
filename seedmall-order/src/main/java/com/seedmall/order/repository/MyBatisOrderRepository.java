/*
 * MyBatis-Plus 订单仓储实现，负责订单表查询和写入。
 */
package com.seedmall.order.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seedmall.order.entity.TradeOrder;
import com.seedmall.order.mapper.TradeOrderMapper;
import org.springframework.stereotype.Repository;

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
}
