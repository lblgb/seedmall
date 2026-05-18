/*
 * 网关鉴权过滤器，为后续 JWT 校验和用户上下文透传预留入口。
 */
package com.seedmall.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关鉴权过滤器。
 */
@Component
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    /**
     * 透传请求并预留鉴权扩展点。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange);
    }

    /**
     * 返回过滤器执行顺序。
     */
    @Override
    public int getOrder() {
        return -100;
    }
}
