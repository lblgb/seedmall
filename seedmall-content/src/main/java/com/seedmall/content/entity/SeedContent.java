/*
 * 种草内容实体，映射内容主表。
 */
package com.seedmall.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 种草内容实体。
 */
@TableName("seed_content")
public class SeedContent {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long authorId;
    private Long productId;
    private String title;
    private String body;
    private Integer auditStatus;

    /**
     * 返回内容编号。
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置内容编号。
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 返回作者编号。
     */
    public Long getAuthorId() {
        return authorId;
    }

    /**
     * 设置作者编号。
     */
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
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
     * 返回标题。
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题。
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 返回正文。
     */
    public String getBody() {
        return body;
    }

    /**
     * 设置正文。
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 返回审核状态。
     */
    public Integer getAuditStatus() {
        return auditStatus;
    }

    /**
     * 设置审核状态。
     */
    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }
}
