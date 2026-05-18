/*
 * 内容审核事件对象，用于内容服务与 AI 服务解耦。
 */
package com.seedmall.api.content;

import java.time.LocalDateTime;

/**
 * 内容审核事件。
 *
 * @param contentId 内容编号
 * @param authorId 作者编号
 * @param text 内容文本
 * @param createdAt 创建时间
 */
public record ContentAuditEvent(Long contentId, Long authorId, String text, LocalDateTime createdAt) {
}
