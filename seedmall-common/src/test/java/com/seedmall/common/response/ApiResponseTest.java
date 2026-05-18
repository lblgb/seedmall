/*
 * ApiResponse 单元测试，验证统一响应对象的基础行为。
 */
package com.seedmall.common.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ApiResponse 测试。
 */
class ApiResponseTest {

    /**
     * 验证成功响应的状态码和数据。
     */
    @Test
    void okShouldWrapData() {
        ApiResponse<String> response = ApiResponse.ok("seed");

        assertThat(response.code()).isZero();
        assertThat(response.data()).isEqualTo("seed");
    }
}
