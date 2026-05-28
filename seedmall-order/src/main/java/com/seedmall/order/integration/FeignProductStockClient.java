/*
 * 基于 OpenFeign 的商品库存客户端实现。
 */
package com.seedmall.order.integration;

import com.seedmall.api.product.DeductStockRequest;
import com.seedmall.api.product.ProductFeignClient;
import org.springframework.stereotype.Component;

/**
 * OpenFeign 商品库存客户端。
 */
@Component
public class FeignProductStockClient implements ProductStockClient {

    private final ProductFeignClient productFeignClient;

    /**
     * 注入商品服务 Feign 客约。
     */
    public FeignProductStockClient(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

    /**
     * 调用商品服务扣减数据库库存。
     */
    @Override
    public void deductStock(Long productId, Integer quantity) {
        productFeignClient.deductStock(new DeductStockRequest(productId, quantity));
    }
}
