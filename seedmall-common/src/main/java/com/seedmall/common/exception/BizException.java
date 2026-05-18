/*
 * 业务异常对象，用于表达可预期的业务失败。
 */
package com.seedmall.common.exception;

/**
 * 业务异常。
 */
public class BizException extends RuntimeException {

    private final int code;

    /**
     * 创建带业务码和消息的异常。
     */
    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 返回业务错误码。
     */
    public int getCode() {
        return code;
    }
}
