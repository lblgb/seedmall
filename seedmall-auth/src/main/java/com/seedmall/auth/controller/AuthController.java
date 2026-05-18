/*
 * 认证接口控制器，提供登录和令牌签发示例。
 */
package com.seedmall.auth.controller;

import com.seedmall.auth.service.TokenService;
import com.seedmall.common.response.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 */
@Validated
@RestController
public class AuthController {

    private final TokenService tokenService;

    /**
     * 注入令牌服务。
     */
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 登录并返回演示令牌。
     */
    @PostMapping("/auth/login")
    public ApiResponse<String> login(@RequestParam @NotBlank(message = "用户名不能为空") String username) {
        return ApiResponse.ok(tokenService.issue(username));
    }
}
