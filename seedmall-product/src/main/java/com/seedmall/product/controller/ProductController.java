/*
 * 商品接口控制器，提供商品查询示例。
 */
package com.seedmall.product.controller;

import com.seedmall.common.response.ApiResponse;
import com.seedmall.product.entity.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品接口。
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    /**
     * 查询商品详情示例数据。
     */
    @GetMapping("/{id}")
    public ApiResponse<Product> detail(@PathVariable Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("种草爆款商品" + id);
        product.setStock(100);
        return ApiResponse.ok(product);
    }
}
