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
});
