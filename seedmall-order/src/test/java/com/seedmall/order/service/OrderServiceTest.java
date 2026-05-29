/*
 * 订单服务测试，验证订单落库对象和幂等创建行为。
 */
package com.seedmall.order.service;

import com.seedmall.api.order.CreateOrderRequest;
import com.seedmall.api.order.OrderQueryResponse;
import com.seedmall.order.entity.TradeOrder;
import com.seedmall.order.integration.ProductStockClient;
import com.seedmall.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 订单服务测试。
 */
class OrderServiceTest {

    /**
     * 首次创建订单时应写入订单明细并返回新订单号。
     */
    @Test
    void shouldCreateOrderWhenNoExistingOrder() {
        FakeOrderRepository repository = new FakeOrderRepository();
        FakeProductStockClient productStockClient = new FakeProductStockClient();
        OrderService service = new OrderService(repository, productStockClient);

        String orderNo = service.create(new CreateOrderRequest(7L, 101L, 2, "SECKILL"));

        assertThat(orderNo).startsWith("SM");
        assertThat(repository.savedOrders).hasSize(1);
        TradeOrder saved = repository.savedOrders.getFirst();
        assertThat(saved.getOrderNo()).isEqualTo(orderNo);
        assertThat(saved.getUserId()).isEqualTo(7L);
        assertThat(saved.getProductId()).isEqualTo(101L);
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.getSource()).isEqualTo("SECKILL");
        assertThat(saved.getStatus()).isZero();
        assertThat(productStockClient.deductRequests).containsExactly("101:2");
    }

    /**
     * 重复创建同一业务订单时应返回已有订单号且不重复插入。
     */
    @Test
    void shouldReturnExistingOrderWhenDuplicateRequestArrives() {
        FakeOrderRepository repository = new FakeOrderRepository();
        TradeOrder existing = new TradeOrder();
        existing.setOrderNo("SM_EXISTING");
        existing.setUserId(7L);
        existing.setProductId(101L);
        existing.setSource("SECKILL");
        repository.existingOrder = existing;
        FakeProductStockClient productStockClient = new FakeProductStockClient();
        OrderService service = new OrderService(repository, productStockClient);

        String orderNo = service.create(new CreateOrderRequest(7L, 101L, 1, "SECKILL"));

        assertThat(orderNo).isEqualTo("SM_EXISTING");
        assertThat(repository.savedOrders).isEmpty();
        assertThat(productStockClient.deductRequests).isEmpty();
    }

    /**
     * 并发插入撞唯一索引时应重新读取已有订单号。
     */
    @Test
    void shouldReturnExistingOrderWhenConcurrentInsertHitsUniqueKey() {
        FakeOrderRepository repository = new FakeOrderRepository();
        repository.duplicateOnSave = true;
        repository.existingAfterDuplicate = orderOf("SM_RACE_WINNER", 7L, 101L, "SECKILL");
        FakeProductStockClient productStockClient = new FakeProductStockClient();
        OrderService service = new OrderService(repository, productStockClient);

        String orderNo = service.create(new CreateOrderRequest(7L, 101L, 1, "SECKILL"));

        assertThat(orderNo).isEqualTo("SM_RACE_WINNER");
        assertThat(productStockClient.deductRequests).isEmpty();
    }

    /**
     * 来源为空时应使用默认秒杀来源。
     */
    @Test
    void shouldDefaultSourceWhenRequestSourceIsBlank() {
        FakeOrderRepository repository = new FakeOrderRepository();
        OrderService service = new OrderService(repository, new FakeProductStockClient());

        service.create(new CreateOrderRequest(7L, 101L, 1, " "));

        assertThat(repository.savedOrders.getFirst().getSource()).isEqualTo("SECKILL");
    }

    /**
     * 查询秒杀订单时应返回已有订单快照。
     */
    @Test
    void shouldQueryExistingSeckillOrder() {
        FakeOrderRepository repository = new FakeOrderRepository();
        repository.existingOrder = orderOf("SM_EXISTING", 7L, 101L, "SECKILL");
        repository.existingOrder.setStatus(0);
        repository.existingOrder.setQuantity(1);
        OrderService service = new OrderService(repository, new FakeProductStockClient());

        Optional<OrderQueryResponse> response = service.querySeckillOrder(7L, 101L);

        assertThat(response).isPresent();
        assertThat(response.get().orderNo()).isEqualTo("SM_EXISTING");
        assertThat(response.get().status()).isZero();
        assertThat(response.get().quantity()).isEqualTo(1);
    }

    /**
     * 没有秒杀订单时应返回空结果。
     */
    @Test
    void shouldReturnEmptyWhenSeckillOrderDoesNotExist() {
        OrderService service = new OrderService(new FakeOrderRepository(), new FakeProductStockClient());

        Optional<OrderQueryResponse> response = service.querySeckillOrder(7L, 101L);

        assertThat(response).isEmpty();
    }

    /**
     * 测试用内存仓储，记录服务写入的订单对象。
     */
    private static final class FakeOrderRepository implements OrderRepository {

        private TradeOrder existingOrder;
        private TradeOrder existingAfterDuplicate;
        private boolean duplicateOnSave;
        private boolean duplicateRaised;
        private final List<TradeOrder> savedOrders = new ArrayList<>();

        /**
         * 按业务幂等键查询已有订单。
         */
        @Override
        public Optional<TradeOrder> findByBusinessKey(Long userId, Long productId, String source) {
            if (existingOrder == null) {
                return duplicateRaised ? Optional.ofNullable(existingAfterDuplicate) : Optional.empty();
            }
            boolean matched = existingOrder.getUserId().equals(userId)
                    && existingOrder.getProductId().equals(productId)
                    && existingOrder.getSource().equals(source);
            return matched ? Optional.of(existingOrder) : Optional.empty();
        }

        /**
         * 保存订单并记录到内存列表。
         */
        @Override
        public void save(TradeOrder order) {
            if (duplicateOnSave) {
                duplicateRaised = true;
                throw new DuplicateKeyException("duplicate business key");
            }
            savedOrders.add(order);
        }
    }

    /**
     * 测试用商品库存客户端，记录扣库存请求。
     */
    private static final class FakeProductStockClient implements ProductStockClient {

        private final List<String> deductRequests = new ArrayList<>();

        /**
         * 记录扣减商品库存的请求。
         */
        @Override
        public void deductStock(Long productId, Integer quantity) {
            deductRequests.add(productId + ":" + quantity);
        }
    }

    /**
     * 构造测试订单对象。
     */
    private static TradeOrder orderOf(String orderNo, Long userId, Long productId, String source) {
        TradeOrder order = new TradeOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setProductId(productId);
        order.setSource(source);
        return order;
    }
}
