/*
 * AI 服务远程调用契约。
 */
package com.seedmall.api.ai;

import com.seedmall.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * AI 服务 Feign 客户端。
 */
@FeignClient(name = "seedmall-ai", path = "/ai")
public interface AiFeignClient {

    /**
     * 调用 AI 内容审核能力。
     */
    @PostMapping("/audit")
    ApiResponse<AuditResult> audit(@Valid @RequestBody AuditRequest request);
}
