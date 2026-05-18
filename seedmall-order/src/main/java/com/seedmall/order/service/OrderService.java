/*
 * 订单服务，封装订单创建和状态初始化逻辑。
 */
package com.seedmall.order.service;

import com.seedmall.api.order.CreateOrderRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单业务服务。
 */
@Service
public class OrderService {

    /**
     * 创建演示订单号。
     */
    public String create(CreateOrderRequest request) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SM" + time + request.userId() + random;
    }
}
