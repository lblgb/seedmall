/*
 * SeedMall API 适配层测试，验证后端不可用时的演示数据降级。
 */
import { describe, expect, it } from 'vitest';
import { createSeedmallApi } from './seedmallApi';

/**
 * 创建总是失败的请求客户端。
 */
function createFailingHttpClient() {
  return {
    get: async () => {
      throw new Error('网关不可用');
    },
    post: async () => {
      throw new Error('网关不可用');
    }
  };
}

describe('createSeedmallApi', () => {
  /**
   * 验证 Feed 接口失败时返回演示内容。
   */
  it('returns demo feed when gateway feed request fails', async () => {
    const api = createSeedmallApi('http://localhost:9000', createFailingHttpClient());

    const feed = await api.fetchFeed();

    expect(feed).toHaveLength(3);
    expect(feed[0].title).toContain('露营');
  });

  /**
   * 验证商品接口失败时返回演示商品。
   */
  it('returns demo product when product request fails', async () => {
    const api = createSeedmallApi('http://localhost:9000', createFailingHttpClient());

    const product = await api.fetchProduct(101);

    expect(product.id).toBe(101);
    expect(product.name).toContain('轻量');
  });

  /**
   * 验证秒杀失败时返回排队演示结果。
   */
  it('returns demo seckill result when seckill request fails', async () => {
    const api = createSeedmallApi('http://localhost:9000', createFailingHttpClient());

    const result = await api.reserveSeckill(101, 1);

    expect(result.status).toBe('DEMO');
    expect(result.message).toContain('演示');
  });

  /**
   * 验证订单查询接口会从统一响应中取出订单结果。
   */
  it('fetches seckill order from gateway response', async () => {
    const api = createSeedmallApi('http://localhost:9000', {
      get: async <T = unknown>(url: string) => {
        expect(url).toContain('/orders/seckill?userId=7&productId=101');
        return {
          data: {
            code: 0,
            message: '成功',
            data: {
              orderNo: 'SM_EXISTING',
              userId: 7,
              productId: 101,
              quantity: 1,
              status: 0,
              source: 'SECKILL'
            }
          } as T
        };
      },
      post: async () => {
        throw new Error('not used');
      }
    });

    const order = await api.fetchSeckillOrder(101, 7);

    expect(order.orderNo).toBe('SM_EXISTING');
    expect(order.statusText).toBe('已创建');
  });

  /**
   * 验证订单查询失败时返回空订单状态，页面仍可展示。
   */
  it('returns empty order state when order request fails', async () => {
    const api = createSeedmallApi('http://localhost:9000', createFailingHttpClient());

    const order = await api.fetchSeckillOrder(101, 7);

    expect(order.orderNo).toBe('');
    expect(order.statusText).toBe('暂无订单');
  });
});
