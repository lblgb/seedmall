/*
 * 用户服务启动入口。
 */
package com.seedmall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务应用。
 */
@MapperScan("com.seedmall.user.mapper")
@SpringBootApplication(scanBasePackages = "com.seedmall")
public class UserApplication {

    /**
     * 启动用户服务。
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
