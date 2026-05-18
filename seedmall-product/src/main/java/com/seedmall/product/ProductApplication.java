/*
 * 商品服务启动入口。
 */
package com.seedmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商品服务应用。
 */
@MapperScan("com.seedmall.product.mapper")
@SpringBootApplication(scanBasePackages = "com.seedmall")
public class ProductApplication {

    /**
     * 启动商品服务。
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
