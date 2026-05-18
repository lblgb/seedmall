# SeedMall Web 前端设计

## 目标

`seedmall-web` 是 SeedMall 的轻量 Vue 演示前端，用来直观看到“种草内容 -> 商品秒杀 -> AI 审核/客服 -> 订单状态”的主链路效果。

## 技术约束

- 使用 Vue 3、Vite、TypeScript、Axios。
- 文档、页面文案和代码注释使用中文。
- 文件统一使用 UTF-8 编码。
- 代码文件开头必须有文件用途说明。
- 手写函数必须至少有一行简单说明。
- 默认网关地址为 `http://localhost:9000`。
- 后端不可用时使用 mock 数据，保证前端可以单独预览。

## 页面结构

页面采用工作台布局，不做营销页：

- 顶部：项目名、网关地址输入框、刷新按钮。
- 左侧：种草 Feed，展示内容标题、作者、关联商品和审核状态。
- 中间：商品与秒杀活动，展示库存、热度、秒杀按钮和订单状态。
- 右侧：AI 面板，包含内容审核、客服问答和链路事件。

## 数据流

前端优先请求网关接口：

- `GET /contents/feed`
- `GET /products/{id}`
- `POST /seckill/{productId}?userId=1`
- `POST /ai/audit`

如果请求失败，前端回退到本地 mock 数据，并在页面提示当前处于演示模式。

## 验证

- `npm test` 验证 API 适配层的 mock fallback 行为。
- `npm run build` 验证 TypeScript 与 Vite 构建。
