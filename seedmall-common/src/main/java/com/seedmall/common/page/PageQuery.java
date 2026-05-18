/*
 * 分页查询参数对象，用于统一分页入口。
 */
package com.seedmall.common.page;

/**
 * 分页查询参数。
 *
 * @param pageNo 页码
 * @param pageSize 每页数量
 */
public record PageQuery(long pageNo, long pageSize) {

    /**
     * 返回修正后的页码。
     */
    public long safePageNo() {
        return pageNo <= 0 ? 1 : pageNo;
    }

    /**
     * 返回修正后的每页数量。
     */
    public long safePageSize() {
        if (pageSize <= 0) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}
