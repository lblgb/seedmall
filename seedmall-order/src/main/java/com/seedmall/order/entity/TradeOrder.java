/*
 * 交易订单实体，映射订单主表。
 */
package com.seedmall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 交易订单实体。
 */
@TableName("trade_order")
public class TradeOrder {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long productId;
    private Integer status;
    private Integer quantity;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 返回订单编号。
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置订单编号。
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 返回订单号。
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单号。
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 返回用户编号。
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户编号。
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 返回商品编号。
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 设置商品编号。
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * 返回订单状态。
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置订单状态。
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 返回购买数量。
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置购买数量。
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 返回订单来源。
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置订单来源。
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 返回创建时间。
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间。
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 返回更新时间。
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间。
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
