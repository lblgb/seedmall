/*
 * AI 供应商配置属性，支持 OpenAI 兼容接口切换不同模型服务。
 */
package com.seedmall.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 供应商配置。
 */
@ConfigurationProperties(prefix = "seedmall.ai")
public class AiProviderProperties {

    private String baseUrl = "https://api.openai.com/v1";
    private String apiKey = "";
    private String model = "gpt-4o-mini";

    /**
     * 返回 API 基础地址。
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 设置 API 基础地址。
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 返回 API 密钥。
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * 设置 API 密钥。
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 返回模型名称。
     */
    public String getModel() {
        return model;
    }

    /**
     * 设置模型名称。
     */
    public void setModel(String model) {
        this.model = model;
    }
}
