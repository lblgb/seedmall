/*
 * 秒杀服务启动入口。
 */
package com.seedmall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 秒杀服务应用。
 */
@SpringBootApplication(scanBasePackages = "com.seedmall")
public class SeckillApplication {

    /**
     * 启动秒杀服务。
     */
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }
}
