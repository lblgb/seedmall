/*
 * 用户资料实体，映射用户基础信息表。
 */
package com.seedmall.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户资料实体。
 */
@TableName("user_profile")
public class UserProfile {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String nickname;
    private String avatarUrl;

    /**
     * 返回用户编号。
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户编号。
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 返回昵称。
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置昵称。
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 返回头像地址。
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 设置头像地址。
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
