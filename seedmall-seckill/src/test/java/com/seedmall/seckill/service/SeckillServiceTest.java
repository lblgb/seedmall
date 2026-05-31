/*
 * 秒杀服务测试，验证入口幂等、库存预扣和 MQ 投递行为。
 */
package com.seedmall.seckill.service;

import com.seedmall.api.mq.RocketMqTopics;
import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.api.seckill.SeckillStockResponse;
import com.seedmall.common.exception.BizException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 秒杀服务测试。
 */
class SeckillServiceTest {

    /**
     * 首次抢购应记录用户抢购标记、扣减库存并发送下单消息。
     */
    @Test
    void shouldReserveStockAndSendOrderMessageForFirstRequest() {
        TestFixture fixture = new TestFixture();
        when(fixture.valueOperations.setIfAbsent("seckill:reservation:101:7", "1", Duration.ofMinutes(30)))
                .thenReturn(true);
        when(fixture.valueOperations.decrement("seckill:stock:101")).thenReturn(9L);

        String result = fixture.service.reserve(7L, 101L);

        assertThat(result).isEqualTo("排队中");
        verify(fixture.rocketMQTemplate).convertAndSend(eq(RocketMqTopics.ORDER_CREATE), any(CreateOrderRequest.class));
    }

    /**
     * 重复抢购应直接返回排队中且不重复扣库存、不重复发消息。
     */
    @Test
    void shouldSkipStockAndMessageWhenDuplicateRequestArrives() {
        TestFixture fixture = new TestFixture();
        when(fixture.valueOperations.setIfAbsent("seckill:reservation:101:7", "1", Duration.ofMinutes(30)))
                .thenReturn(false);

        String result = fixture.service.reserve(7L, 101L);

        assertThat(result).isEqualTo("已在排队中");
        verify(fixture.valueOperations, never()).decrement("seckill:stock:101");
        verify(fixture.rocketMQTemplate, never()).convertAndSend(eq(RocketMqTopics.ORDER_CREATE), any(CreateOrderRequest.class));
    }

    /**
     * 库存不足时应回滚用户抢购标记，允许用户后续重试。
     */
    @Test
    void shouldRollbackReservationKeyWhenStockIsNotEnough() {
        TestFixture fixture = new TestFixture();
        when(fixture.valueOperations.setIfAbsent("seckill:reservation:101:7", "1", Duration.ofMinutes(30)))
                .thenReturn(true);
        when(fixture.valueOperations.decrement("seckill:stock:101")).thenReturn(-1L);

        assertThatThrownBy(() -> fixture.service.reserve(7L, 101L))
                .isInstanceOf(BizException.class)
                .hasMessage("秒杀库存不足");
        verify(fixture.valueOperations).increment("seckill:stock:101");
        verify(fixture.redisTemplate).delete("seckill:reservation:101:7");
        verify(fixture.rocketMQTemplate, never()).convertAndSend(eq(RocketMqTopics.ORDER_CREATE), any(CreateOrderRequest.class));
    }

    /**
     * 初始化秒杀库存时应写入 Redis 库存 key。
     */
    @Test
    void shouldInitializeSeckillStock() {
        TestFixture fixture = new TestFixture();

        SeckillStockResponse response = fixture.service.initializeStock(101L, 20);

        verify(fixture.valueOperations).set("seckill:stock:101", "20");
        assertThat(response.productId()).isEqualTo(101L);
        assertThat(response.redisStock()).isEqualTo(20);
        assertThat(response.reserved()).isFalse();
    }

    /**
     * 查询秒杀库存时应返回 Redis 库存和用户排队标记。
     */
    @Test
    void shouldQuerySeckillStockStatus() {
        TestFixture fixture = new TestFixture();
        when(fixture.valueOperations.get("seckill:stock:101")).thenReturn("9");
        when(fixture.redisTemplate.hasKey("seckill:reservation:101:7")).thenReturn(true);
        when(fixture.redisTemplate.getExpire("seckill:reservation:101:7")).thenReturn(120L);

        SeckillStockResponse response = fixture.service.queryStock(101L, 7L);

        assertThat(response.productId()).isEqualTo(101L);
        assertThat(response.redisStock()).isEqualTo(9);
        assertThat(response.userId()).isEqualTo(7L);
        assertThat(response.reserved()).isTrue();
        assertThat(response.reservationTtlSeconds()).isEqualTo(120L);
    }

    /**
     * 测试夹具，集中创建 Redis 与 MQ 依赖。
     */
    private static final class TestFixture {

        private final StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        private final ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        private final RocketMQTemplate rocketMQTemplate = mock(RocketMQTemplate.class);
        private final SeckillService service = new SeckillService(redisTemplate, rocketMQTemplate);

        /**
         * 初始化 Redis value 操作对象。
         */
        private TestFixture() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        }
    }
}
