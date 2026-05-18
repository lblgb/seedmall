/*
 * AI 内容审核请求对象。
 */
package com.seedmall.api.ai;

import jakarta.validation.constraints.NotBlank;

/**
 * 内容审核请求。
 *
 * @param bizId 业务编号
 * @param content 待审核内容
 */
public record AuditRequest(@NotBlank(message = "业务编号不能为空") String bizId,
                           @NotBlank(message = "审核内容不能为空") String content) {
}
