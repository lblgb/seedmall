<!-- SeedMall 前端工作台，展示内容种草、商品秒杀和 AI 能力主链路。 -->
<script setup lang="ts">
import { Bot, CheckCircle2, Loader2, RefreshCw, Search, Send, ShoppingCart, Zap } from 'lucide-vue-next';
import { computed, onMounted, ref } from 'vue';
import {
  createSeedmallApi,
  type AiAuditResult,
  type FeedItem,
  type ProductItem,
  type SeckillOrder,
  type SeckillResult,
  type SeckillStock
} from './api/seedmallApi';
import { demoTimeline, findDemoProduct } from './mock/demoData';

const gatewayUrl = ref('http://localhost:9000');
const feedItems = ref<FeedItem[]>([]);
const selectedContent = ref<FeedItem | null>(null);
const selectedProduct = ref<ProductItem>(findDemoProduct(101));
const userId = ref(1);
const auditText = ref('这是一篇关于周末露营轻装清单的种草内容。');
const auditResult = ref<AiAuditResult | null>(null);
const seckillResult = ref<SeckillResult | null>(null);
const seckillOrder = ref<SeckillOrder | null>(null);
const seckillStock = ref<SeckillStock | null>(null);
const customerQuestion = ref('这套商品适合新手露营吗？');
const customerAnswer = ref('适合。当前演示客服会结合内容、商品和活动信息给出推荐理由。');
const loading = ref(false);
const modeText = ref('演示模式');

const timeline = computed(() => demoTimeline);

/**
 * 基于当前网关地址创建 API 客户端。
 */
function apiClient() {
  return createSeedmallApi(gatewayUrl.value);
}

/**
 * 刷新内容 Feed 和默认商品信息。
 */
async function refreshDashboard() {
  loading.value = true;
  try {
    const api = apiClient();
    feedItems.value = await api.fetchFeed();
    selectedContent.value = feedItems.value[0] ?? null;
    selectedProduct.value = await api.fetchProduct(selectedContent.value?.productId ?? 101);
    seckillStock.value = await api.fetchSeckillStock(selectedProduct.value.id, userId.value);
    modeText.value = '已连接或已降级';
  } finally {
    loading.value = false;
  }
}

/**
 * 选择内容并加载关联商品。
 */
async function selectContent(item: FeedItem) {
  selectedContent.value = item;
  selectedProduct.value = await apiClient().fetchProduct(item.productId);
  seckillOrder.value = null;
  seckillResult.value = null;
  seckillStock.value = await apiClient().fetchSeckillStock(item.productId, userId.value);
  auditText.value = item.summary;
}

/**
 * 短暂等待异步 MQ 消费，便于页面随后读取订单结果。
 */
function waitForOrderCreation() {
  return new Promise((resolve) => {
    window.setTimeout(resolve, 1200);
  });
}

/**
 * 查询当前商品的秒杀订单结果。
 */
async function refreshSeckillOrder() {
  seckillOrder.value = await apiClient().fetchSeckillOrder(selectedProduct.value.id, userId.value);
}

/**
 * 查询当前商品的 Redis 秒杀库存。
 */
async function refreshSeckillStock() {
  seckillStock.value = await apiClient().fetchSeckillStock(selectedProduct.value.id, userId.value);
}

/**
 * 使用数据库库存初始化 Redis 秒杀库存。
 */
async function initializeSelectedSeckillStock() {
  seckillStock.value = await apiClient().initializeSeckillStock(selectedProduct.value.id, selectedProduct.value.stock);
}

/**
 * 发起秒杀请求并展示订单排队结果。
 */
async function reserveSelectedProduct() {
  loading.value = true;
  try {
    seckillResult.value = await apiClient().reserveSeckill(selectedProduct.value.id, userId.value);
    await waitForOrderCreation();
    await refreshSeckillStock();
    await refreshSeckillOrder();
  } finally {
    loading.value = false;
  }
}

/**
 * 调用 AI 审核能力或本地规则降级。
 */
async function auditSelectedContent() {
  auditResult.value = await apiClient().auditContent(String(selectedContent.value?.id ?? 'demo'), auditText.value);
}

/**
 * 根据当前商品生成演示客服回答。
 */
function answerQuestion() {
  customerAnswer.value = `${selectedProduct.value.name} 当前秒杀价 ${selectedProduct.value.seckillPrice} 元，库存 ${selectedProduct.value.stock} 件。建议先关注内容口碑，再在秒杀入口排队下单。`;
}

onMounted(() => {
  void refreshDashboard();
});
</script>

<template>
  <main class="shell">
    <header class="topbar">
      <div>
        <p class="eyebrow">SeedMall 微服务演示</p>
        <h1>内容种草 · 秒杀交易 · AI 能力工作台</h1>
      </div>
      <div class="gateway-control">
        <label for="gateway">网关地址</label>
        <input id="gateway" v-model="gatewayUrl" type="text" />
        <label for="userId">用户</label>
        <input id="userId" v-model.number="userId" type="number" min="1" />
        <button type="button" class="icon-button" title="刷新数据" @click="refreshDashboard">
          <Loader2 v-if="loading" class="spin" :size="18" />
          <RefreshCw v-else :size="18" />
        </button>
      </div>
    </header>

    <section class="status-strip">
      <div v-for="event in timeline" :key="event.name" class="status-item">
        <CheckCircle2 :size="18" />
        <div>
          <strong>{{ event.name }}</strong>
          <span>{{ event.detail }}</span>
        </div>
      </div>
    </section>

    <section class="workspace">
      <aside class="panel feed-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">Community Feed</p>
            <h2>种草内容</h2>
          </div>
          <span class="mode-pill">{{ modeText }}</span>
        </div>

        <button
          v-for="item in feedItems"
          :key="item.id"
          type="button"
          class="feed-card"
          :class="{ active: selectedContent?.id === item.id }"
          @click="selectContent(item)"
        >
          <img :src="item.imageUrl" :alt="item.title" />
          <span class="feed-meta">{{ item.author }} · 热度 {{ item.heat }}</span>
          <strong>{{ item.title }}</strong>
          <span>{{ item.summary }}</span>
          <em>{{ item.auditStatus }} / 关联 {{ item.productName }}</em>
        </button>
      </aside>

      <section class="panel product-panel">
        <div class="product-visual">
          <img :src="selectedProduct.imageUrl" :alt="selectedProduct.name" />
          <span>{{ selectedProduct.tag }}</span>
        </div>
        <div class="product-body">
          <p class="eyebrow">Flash Sale</p>
          <h2>{{ selectedProduct.name }}</h2>
          <p class="product-copy">
            从内容 Feed 进入商品活动，秒杀服务负责 Redis 预扣库存，订单服务通过 MQ 异步创建订单。
          </p>
          <div class="metric-grid">
            <div>
              <span>原价</span>
              <strong>¥{{ selectedProduct.price }}</strong>
            </div>
            <div>
              <span>秒杀价</span>
              <strong>¥{{ selectedProduct.seckillPrice }}</strong>
            </div>
            <div>
              <span>数据库库存</span>
              <strong>{{ selectedProduct.stock }}</strong>
            </div>
            <div>
              <span>Redis 库存</span>
              <strong>{{ seckillStock?.redisStock ?? '-' }}</strong>
            </div>
            <div>
              <span>排队标记</span>
              <strong>{{ seckillStock?.reservedText ?? '未查询' }}</strong>
            </div>
          </div>
          <div class="stock-actions">
            <button type="button" class="secondary-button compact-button" @click="initializeSelectedSeckillStock">
              初始化 Redis 库存
            </button>
            <button type="button" class="secondary-button compact-button" @click="refreshSeckillStock">
              刷新库存
            </button>
          </div>
          <button type="button" class="primary-button" @click="reserveSelectedProduct">
            <Zap :size="18" />
            参与秒杀
          </button>
          <div class="result-line">
            <ShoppingCart :size="18" />
            <span>{{ seckillResult?.message ?? '等待用户发起秒杀请求' }}</span>
          </div>
          <div class="order-result">
            <div>
              <span>订单状态</span>
              <strong>{{ seckillOrder?.statusText ?? '尚未查询' }}</strong>
            </div>
            <div>
              <span>订单号</span>
              <strong>{{ seckillOrder?.orderNo || '-' }}</strong>
            </div>
            <button type="button" class="secondary-button compact-button" @click="refreshSeckillOrder">
              <RefreshCw :size="16" />
              刷新订单
            </button>
          </div>
        </div>
      </section>

      <aside class="panel ai-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">AI Service</p>
            <h2>审核与客服</h2>
          </div>
          <Bot :size="22" />
        </div>

        <label class="field-label" for="auditText">审核文本</label>
        <textarea id="auditText" v-model="auditText" rows="5"></textarea>
        <button type="button" class="secondary-button" @click="auditSelectedContent">
          <Search :size="17" />
          AI 审核
        </button>
        <p class="audit-result" :class="{ blocked: auditResult && !auditResult.passed }">
          {{ auditResult ? auditResult.reason : '尚未审核' }}
        </p>

        <label class="field-label" for="question">客服问题</label>
        <input id="question" v-model="customerQuestion" type="text" />
        <button type="button" class="secondary-button" @click="answerQuestion">
          <Send :size="17" />
          生成回答
        </button>
        <p class="answer">{{ customerAnswer }}</p>
      </aside>
    </section>
  </main>
</template>
