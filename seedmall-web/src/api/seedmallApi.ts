/*
 * SeedMall API 适配层，集中处理网关请求和本地演示数据降级。
 */
import axios from 'axios';
import { createDemoAuditResult, createDemoSeckillResult, demoFeed, findDemoProduct } from '../mock/demoData';

export type HttpClient = {
  get: <T = unknown>(url: string) => Promise<{ data: T }>;
  post: <T = unknown>(url: string, body?: unknown) => Promise<{ data: T }>;
};

export type ApiResponse<T> = {
  code: number;
  message: string;
  data: T;
};

export type FeedItem = {
  id: number;
  author: string;
  title: string;
  summary: string;
  productId: number;
  productName: string;
  auditStatus: string;
  imageUrl: string;
  heat: number;
};

export type ProductItem = {
  id: number;
  name: string;
  stock: number;
  price: number;
  seckillPrice: number;
  tag: string;
  imageUrl: string;
};

export type AiAuditResult = {
  passed: boolean;
  reason: string;
};

export type SeckillResult = {
  status: 'SUCCESS' | 'DEMO';
  message: string;
};

export type SeckillOrder = {
  orderNo: string;
  userId: number;
  productId: number;
  quantity: number;
  status: number;
  statusText: string;
  source: string;
};

export type TimelineEvent = {
  name: string;
  status: 'ready' | 'running' | 'done' | 'warn';
  detail: string;
};

export type SeedmallApi = {
  fetchFeed: () => Promise<FeedItem[]>;
  fetchProduct: (productId: number) => Promise<ProductItem>;
  reserveSeckill: (productId: number, userId: number) => Promise<SeckillResult>;
  fetchSeckillOrder: (productId: number, userId: number) => Promise<SeckillOrder>;
  auditContent: (bizId: string, content: string) => Promise<AiAuditResult>;
};

/**
 * 规范化网关地址，避免结尾斜杠影响路径拼接。
 */
function normalizeGatewayUrl(gatewayUrl: string): string {
  return gatewayUrl.replace(/\/+$/, '');
}

/**
 * 从统一响应体中取出业务数据。
 */
function unwrapApiResponse<T>(response: { data: ApiResponse<T> | T }): T {
  const payload = response.data as ApiResponse<T>;
  if (typeof payload === 'object' && payload !== null && 'data' in payload) {
    return payload.data;
  }
  return response.data as T;
}

/**
 * 将后端当前的字符串 Feed 示例转换为前端卡片数据。
 */
function normalizeFeed(rawFeed: unknown): FeedItem[] {
  if (Array.isArray(rawFeed) && rawFeed.every((item) => typeof item === 'object')) {
    return rawFeed as FeedItem[];
  }
  return demoFeed;
}

/**
 * 将后端商品详情补齐为前端展示模型。
 */
function normalizeProduct(rawProduct: Partial<ProductItem>, productId: number): ProductItem {
  const demoProduct = findDemoProduct(productId);
  return {
    ...demoProduct,
    ...rawProduct,
    id: rawProduct.id ?? productId,
    price: rawProduct.price ?? demoProduct.price,
    seckillPrice: rawProduct.seckillPrice ?? demoProduct.seckillPrice,
    tag: rawProduct.tag ?? demoProduct.tag,
    imageUrl: rawProduct.imageUrl ?? demoProduct.imageUrl
  };
}

/**
 * 将订单状态码转换为前端展示文案。
 */
function orderStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '已创建',
    1: '已支付',
    2: '已取消'
  };
  return statusMap[status] ?? '未知状态';
}

/**
 * 构造空订单状态，表示异步订单尚未创建或查询失败。
 */
function emptySeckillOrder(productId: number, userId: number): SeckillOrder {
  return {
    orderNo: '',
    userId,
    productId,
    quantity: 0,
    status: -1,
    statusText: '暂无订单',
    source: 'SECKILL'
  };
}

/**
 * 将后端订单响应转换为前端展示模型。
 */
function normalizeSeckillOrder(rawOrder: Partial<SeckillOrder> | null, productId: number, userId: number): SeckillOrder {
  if (!rawOrder || !rawOrder.orderNo) {
    return emptySeckillOrder(productId, userId);
  }
  const status = rawOrder.status ?? 0;
  return {
    orderNo: rawOrder.orderNo,
    userId: rawOrder.userId ?? userId,
    productId: rawOrder.productId ?? productId,
    quantity: rawOrder.quantity ?? 1,
    status,
    statusText: orderStatusText(status),
    source: rawOrder.source ?? 'SECKILL'
  };
}

/**
 * 创建 SeedMall API 客户端。
 */
export function createSeedmallApi(gatewayUrl: string, httpClient: HttpClient = axios): SeedmallApi {
  const baseUrl = normalizeGatewayUrl(gatewayUrl);

  return {
    /**
     * 获取内容 Feed，失败时返回本地演示数据。
     */
    async fetchFeed() {
      try {
        const response = await httpClient.get<ApiResponse<unknown>>(`${baseUrl}/contents/feed`);
        return normalizeFeed(unwrapApiResponse(response));
      } catch {
        return demoFeed;
      }
    },

    /**
     * 获取商品详情，失败时返回本地演示商品。
     */
    async fetchProduct(productId: number) {
      try {
        const response = await httpClient.get<ApiResponse<Partial<ProductItem>>>(`${baseUrl}/products/${productId}`);
        return normalizeProduct(unwrapApiResponse(response), productId);
      } catch {
        return findDemoProduct(productId);
      }
    },

    /**
     * 发起秒杀请求，失败时返回演示排队结果。
     */
    async reserveSeckill(productId: number, userId: number) {
      try {
        const response = await httpClient.post<ApiResponse<string>>(`${baseUrl}/seckill/${productId}?userId=${userId}`);
        return { status: 'SUCCESS', message: unwrapApiResponse(response) };
      } catch {
        return createDemoSeckillResult(productId);
      }
    },

    /**
     * 查询当前用户的秒杀订单，失败时返回空订单状态。
     */
    async fetchSeckillOrder(productId: number, userId: number) {
      try {
        const response = await httpClient.get<ApiResponse<Partial<SeckillOrder> | null>>(
          `${baseUrl}/orders/seckill?userId=${userId}&productId=${productId}`
        );
        return normalizeSeckillOrder(unwrapApiResponse(response), productId, userId);
      } catch {
        return emptySeckillOrder(productId, userId);
      }
    },

    /**
     * 发起 AI 内容审核，失败时使用本地规则降级。
     */
    async auditContent(bizId: string, content: string) {
      try {
        const response = await httpClient.post<ApiResponse<AiAuditResult>>(`${baseUrl}/ai/audit`, { bizId, content });
        return unwrapApiResponse(response);
      } catch {
        return createDemoAuditResult(content);
      }
    }
  };
}
