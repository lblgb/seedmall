/*
 * 商品服务测试，验证商品查询和数据库库存扣减行为。
 */
package com.seedmall.product.service;

import com.seedmall.common.exception.BizException;
import com.seedmall.product.entity.Product;
import com.seedmall.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 商品服务测试。
 */
class ProductServiceTest {

    /**
     * 库存充足时应扣减数据库库存。
     */
    @Test
    void shouldDeductStockWhenStockIsEnough() {
        FakeProductRepository repository = new FakeProductRepository();
        repository.products.put(101L, productOf(101L, 10));
        ProductService service = new ProductService(repository);

        service.deductStock(101L, 2);

        assertThat(repository.products.get(101L).getStock()).isEqualTo(8);
    }

    /**
     * 库存不足时应抛出业务异常。
     */
    @Test
    void shouldRejectDeductWhenStockIsNotEnough() {
        FakeProductRepository repository = new FakeProductRepository();
        repository.products.put(101L, productOf(101L, 1));
        ProductService service = new ProductService(repository);

        assertThatThrownBy(() -> service.deductStock(101L, 2))
                .isInstanceOf(BizException.class)
                .hasMessage("商品库存不足");
    }

    /**
     * 测试用商品仓储。
     */
    private static final class FakeProductRepository implements ProductRepository {

        private final Map<Long, Product> products = new HashMap<>();

        /**
         * 按编号查询商品。
         */
        @Override
        public Optional<Product> findById(Long id) {
            return Optional.ofNullable(products.get(id));
        }

        /**
         * 扣减商品库存。
         */
        @Override
        public boolean deductStock(Long productId, Integer quantity) {
            Product product = products.get(productId);
            if (product == null || product.getStock() < quantity) {
                return false;
            }
            product.setStock(product.getStock() - quantity);
            return true;
        }
    }

    /**
     * 构造测试商品。
     */
    private static Product productOf(Long id, Integer stock) {
        Product product = new Product();
        product.setId(id);
        product.setStock(stock);
        return product;
    }
}
