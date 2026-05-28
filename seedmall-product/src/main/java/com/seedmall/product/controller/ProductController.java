/*
 * 商品接口控制器，提供商品查询示例。
 */
package com.seedmall.product.controller;

import com.seedmall.api.product.DeductStockRequest;
import com.seedmall.common.response.ApiResponse;
import com.seedmall.product.entity.Product;
import com.seedmall.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品接口。
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 注入商品业务服务。
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 查询商品详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<Product> detail(@PathVariable Long id) {
        return ApiResponse.ok(productService.detail(id));
    }

    /**
     * 扣减商品数据库库存。
     */
    @PostMapping("/stock/deduct")
    public ApiResponse<Void> deductStock(@Valid @RequestBody DeductStockRequest request) {
        productService.deductStock(request.productId(), request.quantity());
        return ApiResponse.ok();
    }
}
