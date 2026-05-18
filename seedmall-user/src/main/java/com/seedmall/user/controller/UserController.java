/*
 * 用户接口控制器，提供用户资料查询示例。
 */
package com.seedmall.user.controller;

import com.seedmall.common.response.ApiResponse;
import com.seedmall.user.entity.UserProfile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口。
 */
@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * 查询用户资料演示数据。
     */
    @GetMapping("/{id}")
    public ApiResponse<UserProfile> detail(@PathVariable Long id) {
        UserProfile profile = new UserProfile();
        profile.setId(id);
        profile.setNickname("种草用户" + id);
        return ApiResponse.ok(profile);
    }
}
