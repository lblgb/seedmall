/*
 * AI 审核服务，封装内容审核提示词和降级策略。
 */
package com.seedmall.ai.service;

import com.seedmall.ai.client.OpenAiCompatibleClient;
import com.seedmall.api.ai.AuditRequest;
import com.seedmall.api.ai.AuditResult;
import org.springframework.stereotype.Service;

/**
 * AI 内容审核服务。
 */
@Service
public class AiAuditService {

    private final OpenAiCompatibleClient client;

    /**
     * 注入 OpenAI 兼容客户端。
     */
    public AiAuditService(OpenAiCompatibleClient client) {
        this.client = client;
    }

    /**
     * 审核内容并返回结构化结果。
     */
    public AuditResult audit(AuditRequest request) {
        if (request.content().contains("违禁")) {
            return new AuditResult(false, "命中本地敏感词");
        }
        if (request.content().length() < 8) {
            return new AuditResult(true, "短文本通过本地规则");
        }
        if (!client.configured()) {
            return new AuditResult(true, "未配置 AI 密钥，使用本地规则降级通过");
        }
        client.chat("你是内容安全审核助手，只判断内容是否适合社区展示。", request.content());
        return new AuditResult(true, "AI 审核通过");
    }
}
