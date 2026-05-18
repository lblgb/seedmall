/*
 * 统一接口响应对象，保证所有服务返回结构一致。
 */
package com.seedmall.common.response;

/**
 * 统一接口响应体。
 *
 * @param code 业务状态码
 * @param message 响应说明
 * @param data 响应数据
 * @param <T> 数据类型
 */
public record ApiResponse<T>(int code, String message, T data) {

    /**
     * 构造成功响应。
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "成功", data);
    }

    /**
     * 构造无数据成功响应。
     */
    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(0, "成功", null);
    }

    /**
     * 构造失败响应。
     */
    public static ApiResponse<Void> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
