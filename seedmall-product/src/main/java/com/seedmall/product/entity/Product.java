/*
 * 商品实体，映射商品基础信息表。
 */
package com.seedmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 商品实体。
 */
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer stock;

    /**
     * 返回商品编号。
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置商品编号。
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 返回商品名称。
     */
    public String getName() {
        return name;
    }

    /**
     * 设置商品名称。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 返回库存数量。
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * 设置库存数量。
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
