/*
 * AI 服务启动入口。
 */
package com.seedmall.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * AI 服务应用。
 */
@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = "com.seedmall")
public class AiApplication {

    /**
     * 启动 AI 服务。
     */
    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
    }
}
