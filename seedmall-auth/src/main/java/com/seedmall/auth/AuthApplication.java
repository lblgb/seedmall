/*
 * 认证服务启动入口。
 */
package com.seedmall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证服务应用。
 */
@SpringBootApplication(scanBasePackages = "com.seedmall")
public class AuthApplication {

    /**
     * 启动认证服务。
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
