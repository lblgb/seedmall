/*
 * AI 接口控制器，提供内容审核、标题生成和客服问答入口。
 */
package com.seedmall.ai.controller;

import com.seedmall.ai.service.AiAuditService;
import com.seedmall.api.ai.AuditRequest;
import com.seedmall.api.ai.AuditResult;
import com.seedmall.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 能力接口。
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiAuditService auditService;

    /**
     * 注入 AI 审核服务。
     */
    public AiController(AiAuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * 执行内容审核。
     */
    @PostMapping("/audit")
    public ApiResponse<AuditResult> audit(@Valid @RequestBody AuditRequest request) {
        return ApiResponse.ok(auditService.audit(request));
    }
}
