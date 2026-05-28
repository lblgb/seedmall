/*
 * 商品库存客户端接口，隔离订单服务与远程商品服务调用。
 */
package com.seedmall.order.integration;

/**
 * 商品库存客户端接口。
 */
public interface ProductStockClient {

    /**
     * 扣减商品数据库库存。
     */
    void deductStock(Long productId, Integer quantity);
}
