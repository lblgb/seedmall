/*
 * 基于 MyBatis-Plus 的商品仓储实现。
 */
package com.seedmall.product.repository;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.seedmall.product.entity.Product;
import com.seedmall.product.mapper.ProductMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MyBatis 商品仓储。
 */
@Repository
public class MyBatisProductRepository implements ProductRepository {

    private final ProductMapper productMapper;

    /**
     * 注入商品 Mapper。
     */
    public MyBatisProductRepository(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    /**
     * 按商品编号查询商品。
     */
    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(productMapper.selectById(id));
    }

    /**
     * 通过 stock >= quantity 条件保证库存不会扣成负数。
     */
    @Override
    public boolean deductStock(Long productId, Integer quantity) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, productId)
                .ge(Product::getStock, quantity)
                .setSql("stock = stock - " + quantity);
        return productMapper.update(null, wrapper) > 0;
    }
}
