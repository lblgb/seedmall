/*
 * OpenAI 兼容客户端，统一封装大模型 HTTP 调用。
 */
package com.seedmall.ai.client;

import com.seedmall.ai.config.AiProviderProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * OpenAI 兼容客户端。
 */
@Component
public class OpenAiCompatibleClient {

    private final AiProviderProperties properties;
    private final RestClient restClient;

    /**
     * 注入 AI 配置并创建 HTTP 客户端。
     */
    public OpenAiCompatibleClient(AiProviderProperties properties, RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder.baseUrl(properties.getBaseUrl()).build();
    }

    /**
     * 执行聊天补全调用。
     */
    public Map<?, ?> chat(String systemPrompt, String userPrompt) {
        Map<String, Object> body = Map.of(
                "model", properties.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                )
        );
        return restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    /**
     * 判断当前 AI 供应商配置是否可用。
     */
    public boolean configured() {
        return properties.getApiKey() != null && !properties.getApiKey().isBlank();
    }
}
