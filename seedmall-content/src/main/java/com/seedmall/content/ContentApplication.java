/*
 * 内容服务启动入口。
 */
package com.seedmall.content;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 内容服务应用。
 */
@MapperScan("com.seedmall.content.mapper")
@SpringBootApplication(scanBasePackages = "com.seedmall")
public class ContentApplication {

    /**
     * 启动内容服务。
     */
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
