/*
 * 商品业务服务，提供商品查询和库存扣减能力。
 */
package com.seedmall.product.service;

import com.seedmall.common.exception.BizException;
import com.seedmall.product.entity.Product;
import com.seedmall.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

/**
 * 商品业务服务。
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 注入商品仓储。
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 查询商品详情。
     */
    public Product detail(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BizException(404, "商品不存在"));
    }

    /**
     * 扣减商品数据库库存。
     */
    public void deductStock(Long productId, Integer quantity) {
        boolean deducted = productRepository.deductStock(productId, quantity);
        if (!deducted) {
            throw new BizException(409, "商品库存不足");
        }
    }
}
