/*
 * 前端演示数据，在后端网关不可用时保证页面可预览。
 */
import type { AiAuditResult, FeedItem, ProductItem, SeckillResult, TimelineEvent } from '../api/seedmallApi';

export const demoFeed: FeedItem[] = [
  {
    id: 1001,
    author: '夏日装备局',
    title: '周末露营轻装清单',
    summary: '把帐篷、炉具、收纳和防潮垫做成一套组合，适合新手直接下单。',
    productId: 101,
    productName: '轻量露营套装',
    auditStatus: 'AI 已通过',
    imageUrl: 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80',
    heat: 9820
  },
  {
    id: 1002,
    author: '通勤研究所',
    title: '办公室咖啡平替方案',
    summary: '用手冲壶、便携磨豆机和保温杯搭出稳定组合，适合高频通勤。',
    productId: 102,
    productName: '便携咖啡套装',
    auditStatus: '待审核',
    imageUrl: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80',
    heat: 7340
  },
  {
    id: 1003,
    author: '数码搭配师',
    title: '桌面效率升级',
    summary: '显示器支架、机械键盘和扩展坞组合，提升远程办公效率。',
    productId: 103,
    productName: '桌面效率套装',
    auditStatus: 'AI 已通过',
    imageUrl: 'https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=900&q=80',
    heat: 6510
  }
];

export const demoProducts: ProductItem[] = [
  {
    id: 101,
    name: '轻量露营套装',
    stock: 36,
    price: 399,
    seckillPrice: 199,
    tag: '今晚 20:00 开抢',
    imageUrl: demoFeed[0].imageUrl
  },
  {
    id: 102,
    name: '便携咖啡套装',
    stock: 58,
    price: 259,
    seckillPrice: 139,
    tag: '热榜种草',
    imageUrl: demoFeed[1].imageUrl
  },
  {
    id: 103,
    name: '桌面效率套装',
    stock: 24,
    price: 699,
    seckillPrice: 499,
    tag: '限量补贴',
    imageUrl: demoFeed[2].imageUrl
  }
];

export const demoTimeline: TimelineEvent[] = [
  { name: '网关路由', status: 'ready', detail: '请求统一从 seedmall-gateway 进入' },
  { name: '内容审核', status: 'ready', detail: '内容发布后发送 MQ 审核事件' },
  { name: '秒杀预扣', status: 'ready', detail: 'Redis 承接库存热点' },
  { name: '异步下单', status: 'ready', detail: 'RocketMQ 削峰后创建订单' }
];

/**
 * 按商品编号查找演示商品。
 */
export function findDemoProduct(productId: number): ProductItem {
  return demoProducts.find((product) => product.id === productId) ?? demoProducts[0];
}

/**
 * 创建演示秒杀结果。
 */
export function createDemoSeckillResult(productId: number): SeckillResult {
  const product = findDemoProduct(productId);
  return {
    status: 'DEMO',
    message: `${product.name} 已进入演示排队，后端启动后会走 Redis + MQ 链路`
  };
}

/**
 * 创建演示 AI 审核结果。
 */
export function createDemoAuditResult(content: string): AiAuditResult {
  if (content.includes('违禁')) {
    return { passed: false, reason: '命中本地敏感词' };
  }
  return { passed: true, reason: '演示模式：本地规则通过' };
}
