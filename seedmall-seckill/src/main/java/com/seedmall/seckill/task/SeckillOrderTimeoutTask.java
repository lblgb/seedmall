/*
 * 秒杀订单超时任务，定期取消未支付订单并释放 Redis 排队标记。
 */
package com.seedmall.seckill.task;

import com.seedmall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单超时任务。
 */
@Component
public class SeckillOrderTimeoutTask {

    private final SeckillService seckillService;
    private final int timeoutMinutes;
    private final int batchLimit;

    /**
     * 注入秒杀服务和任务参数。
     */
    public SeckillOrderTimeoutTask(SeckillService seckillService,
                                   @Value("${seedmall.seckill.timeout-minutes:30}") int timeoutMinutes,
                                   @Value("${seedmall.seckill.timeout-batch-limit:50}") int batchLimit) {
        this.seckillService = seckillService;
        this.timeoutMinutes = timeoutMinutes;
        this.batchLimit = batchLimit;
    }

    /**
     * 定期取消超时未支付订单。
     */
    @Scheduled(fixedDelayString = "${seedmall.seckill.timeout-scan-delay-ms:60000}",
            initialDelayString = "${seedmall.seckill.timeout-scan-initial-delay-ms:60000}")
    public void cancelExpiredOrders() {
        seckillService.cancelExpiredSeckillOrders(timeoutMinutes, batchLimit);
    }
}
