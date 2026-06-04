/*
 * 订单服务，封装订单创建和状态初始化逻辑。
 */
package com.seedmall.order.service;

import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.api.order.OrderEventResponse;
import com.seedmall.api.order.OrderQueryResponse;
import com.seedmall.order.entity.TradeOrder;
import com.seedmall.order.integration.ProductStockClient;
import com.seedmall.order.repository.OrderRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Optional;

/**
 * 订单业务服务。
 */
@Service
public class OrderService {

    private static final String DEFAULT_SOURCE = "SECKILL";
    private final OrderRepository orderRepository;
    private final ProductStockClient productStockClient;
    private final List<OrderEventResponse> orderEvents = Collections.synchronizedList(new ArrayList<>());

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
        Optional<TradeOrder> existingOrder = orderRepository.findByBusinessKey(request.userId(), request.productId(), source);
        if (existingOrder.isEmpty()) {
            return createNewOrder(request, source);
        }
        TradeOrder order = existingOrder.get();
        if (Integer.valueOf(2).equals(order.getStatus())) {
            return reactivateCanceledOrder(request, source, order);
        }
        return order.getOrderNo();
    }

    /**
     * 查询用户在指定商品上的秒杀订单。
     */
    public Optional<OrderQueryResponse> querySeckillOrder(Long userId, Long productId) {
        return orderRepository.findByBusinessKey(userId, productId, DEFAULT_SOURCE)
                .map(this::toQueryResponse);
    }

    /**
     * 取消用户在指定商品上的秒杀订单，并在首次取消时恢复数据库库存。
     */
    public Optional<OrderQueryResponse> cancelSeckillOrder(Long userId, Long productId) {
        Optional<TradeOrder> existingOrder = orderRepository.findByBusinessKey(userId, productId, DEFAULT_SOURCE);
        if (existingOrder.isEmpty()) {
            return Optional.empty();
        }
        TradeOrder order = existingOrder.get();
        if (Integer.valueOf(2).equals(order.getStatus())) {
            return Optional.of(toQueryResponse(order));
        }
        boolean canceled = orderRepository.cancelByBusinessKey(userId, productId, DEFAULT_SOURCE);
        if (canceled) {
            productStockClient.restoreStock(order.getProductId(), order.getQuantity());
            order.setStatus(2);
            recordEvent(order, "ORDER_CANCELED", "取消秒杀订单并恢复数据库库存");
        }
        return Optional.of(toQueryResponse(order));
    }

    /**
     * 支付用户在指定商品上的秒杀订单，重复支付时保持当前已支付状态。
     */
    public Optional<OrderQueryResponse> paySeckillOrder(Long userId, Long productId) {
        Optional<TradeOrder> existingOrder = orderRepository.findByBusinessKey(userId, productId, DEFAULT_SOURCE);
        if (existingOrder.isEmpty()) {
            return Optional.empty();
        }
        TradeOrder order = existingOrder.get();
        if (!Integer.valueOf(0).equals(order.getStatus())) {
            return Optional.of(toQueryResponse(order));
        }
        boolean paid = orderRepository.payByBusinessKey(userId, productId, DEFAULT_SOURCE);
        if (paid) {
            order.setStatus(1);
            recordEvent(order, "ORDER_PAID", "支付秒杀订单");
        }
        return Optional.of(toQueryResponse(order));
    }

    /**
     * 批量取消超时未支付的秒杀订单。
     */
    public List<OrderQueryResponse> cancelExpiredSeckillOrders(Duration timeout, int limit) {
        LocalDateTime cutoff = LocalDateTime.now().minus(timeout);
        return orderRepository.findCreatedBefore(DEFAULT_SOURCE, cutoff, limit)
                .stream()
                .filter(order -> orderRepository.cancelByBusinessKey(order.getUserId(), order.getProductId(), order.getSource()))
                .peek(order -> {
                    productStockClient.restoreStock(order.getProductId(), order.getQuantity());
                    order.setStatus(2);
                    recordEvent(order, "ORDER_TIMEOUT_CANCELED", "超时未支付自动取消并恢复数据库库存");
                })
                .map(this::toQueryResponse)
                .toList();
    }

    /**
     * 查询用户指定商品的订单事件。
     */
    public List<OrderEventResponse> queryOrderEvents(Long userId, Long productId) {
        synchronized (orderEvents) {
            return orderEvents.stream()
                    .filter(event -> event.userId().equals(userId))
                    .filter(event -> event.productId().equals(productId))
                    .toList();
        }
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
            recordEvent(order, "ORDER_CREATED", "创建秒杀订单并扣减数据库库存");
        } catch (DuplicateKeyException ex) {
            return orderRepository.findByBusinessKey(request.userId(), request.productId(), source)
                    .map(TradeOrder::getOrderNo)
                    .orElseThrow(() -> ex);
        }
        return order.getOrderNo();
    }

    /**
     * 重新激活已取消订单，并重新扣减数据库库存。
     */
    private String reactivateCanceledOrder(CreateOrderRequest request, String source, TradeOrder order) {
        String newOrderNo = nextOrderNo(request.userId());
        boolean reactivated = orderRepository.reactivateCanceledByBusinessKey(
                request.userId(),
                request.productId(),
                source,
                newOrderNo,
                request.quantity()
        );
        if (reactivated) {
            productStockClient.deductStock(request.productId(), request.quantity());
            order.setOrderNo(newOrderNo);
            order.setQuantity(request.quantity());
            order.setStatus(0);
            recordEvent(order, "ORDER_REACTIVATED", "重新激活已取消秒杀订单并扣减数据库库存");
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

    /**
     * 记录订单事件，供后续 Agent 诊断状态流转。
     */
    private void recordEvent(TradeOrder order, String eventType, String detail) {
        orderEvents.add(new OrderEventResponse(
                order.getOrderNo(),
                order.getUserId(),
                order.getProductId(),
                eventType,
                detail,
                LocalDateTime.now()
        ));
    }

    /**
     * 转换为订单查询响应对象。
     */
    private OrderQueryResponse toQueryResponse(TradeOrder order) {
        return new OrderQueryResponse(
                order.getOrderNo(),
                order.getUserId(),
                order.getProductId(),
                order.getQuantity(),
                order.getStatus(),
                order.getSource()
        );
    }
}
