/*
 * AI 内容审核结果对象。
 */
package com.seedmall.api.ai;

/**
 * 内容审核结果。
 *
 * @param passed 是否通过
 * @param reason 审核原因
 */
public record AuditResult(boolean passed, String reason) {
}
