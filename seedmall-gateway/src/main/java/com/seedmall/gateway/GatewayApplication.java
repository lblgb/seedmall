/*
 * 网关服务启动入口。
 */
package com.seedmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关服务应用。
 */
@SpringBootApplication
public class GatewayApplication {

    /**
     * 启动网关服务。
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
