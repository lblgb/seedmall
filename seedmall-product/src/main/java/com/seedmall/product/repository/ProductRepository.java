/*
 * 商品仓储接口，隔离商品业务和 MyBatis 持久化细节。
 */
package com.seedmall.product.repository;

import com.seedmall.product.entity.Product;

import java.util.Optional;

/**
 * 商品仓储。
 */
public interface ProductRepository {

    /**
     * 按商品编号查询商品。
     */
    Optional<Product> findById(Long id);

    /**
     * 在库存充足时原子扣减库存。
     */
    boolean deductStock(Long productId, Integer quantity);
}
