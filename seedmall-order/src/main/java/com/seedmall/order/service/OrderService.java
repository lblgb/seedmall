/*
 * 订单服务，封装订单创建和状态初始化逻辑。
 */
package com.seedmall.order.service;

import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.order.entity.TradeOrder;
import com.seedmall.order.integration.ProductStockClient;
import com.seedmall.order.repository.OrderRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单业务服务。
 */
@Service
public class OrderService {

    private static final String DEFAULT_SOURCE = "SECKILL";
    private final OrderRepository orderRepository;
    private final ProductStockClient productStockClient;

    /**
     * 注入订单仓储和商品库存客户端。
     */
    public OrderService(OrderRepository orderRepository, ProductStockClient productStockClient) {
        this.orderRepository = orderRepository;
        this.productStockClient = productStockClient;
    }

    /**
     * 创建订单，重复业务请求直接返回已有订单号。
     */
    public String create(CreateOrderRequest request) {
        String source = normalizeSource(request.source());
        return orderRepository.findByBusinessKey(request.userId(), request.productId(), source)
                .map(TradeOrder::getOrderNo)
                .orElseGet(() -> createNewOrder(request, source));
    }

    /**
     * 构造并保存新订单。
     */
    private String createNewOrder(CreateOrderRequest request, String source) {
        TradeOrder order = new TradeOrder();
        order.setOrderNo(nextOrderNo(request.userId()));
        order.setUserId(request.userId());
        order.setProductId(request.productId());
        order.setQuantity(request.quantity());
        order.setSource(source);
        order.setStatus(0);
        try {
            orderRepository.save(order);
            productStockClient.deductStock(order.getProductId(), order.getQuantity());
        } catch (DuplicateKeyException ex) {
            return orderRepository.findByBusinessKey(request.userId(), request.productId(), source)
                    .map(TradeOrder::getOrderNo)
                    .orElseThrow(() -> ex);
        }
        return order.getOrderNo();
    }

    /**
     * 规范化订单来源。
     */
    private String normalizeSource(String source) {
        if (source == null || source.isBlank()) {
            return DEFAULT_SOURCE;
        }
        return source;
    }

    /**
     * 生成学习项目使用的订单号。
     */
    private String nextOrderNo(Long userId) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SM" + time + userId + random;
    }
}
