/*
 * 订单服务启动入口。
 */
package com.seedmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单服务应用。
 */
@MapperScan("com.seedmall.order.mapper")
@EnableFeignClients(basePackages = "com.seedmall.api")
@SpringBootApplication(scanBasePackages = "com.seedmall")
public class OrderApplication {

    /**
     * 启动订单服务。
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
