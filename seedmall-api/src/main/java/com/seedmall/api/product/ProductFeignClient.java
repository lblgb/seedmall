/*
 * 商品服务远程调用契约。
 */
package com.seedmall.api.product;

import com.seedmall.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 商品服务 Feign 客户端。
 */
@FeignClient(name = "seedmall-product", path = "/products")
public interface ProductFeignClient {

    /**
     * 扣减商品数据库库存。
     */
    @PostMapping("/stock/deduct")
    ApiResponse<Void> deductStock(@Valid @RequestBody DeductStockRequest request);
}
