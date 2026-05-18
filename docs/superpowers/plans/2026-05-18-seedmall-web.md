# SeedMall Web Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 创建一个 Vue 3 前端工作台，用于演示 SeedMall 主链路效果。

**Architecture:** 前端独立放在 `seedmall-web`，使用 Vite 启动和构建。API 访问集中在 `src/api/seedmallApi.ts`，页面状态集中在 `App.vue`，后端不可用时从 `src/mock/demoData.ts` 获取演示数据。

**Tech Stack:** Vue 3、Vite、TypeScript、Axios、Vitest。

---

### Task 1: 前端工程与测试

**Files:**
- Create: `seedmall-web/package.json`
- Create: `seedmall-web/vite.config.ts`
- Create: `seedmall-web/tsconfig.json`
- Create: `seedmall-web/src/api/seedmallApi.test.ts`

- [x] **Step 1: 写 API fallback 测试**

测试要求 Feed 和商品接口在网关不可用时返回本地演示数据。

- [x] **Step 2: 运行测试确认失败**

Run: `npm test`

Expected: FAIL，因为 API 实现文件尚不存在。

### Task 2: API 与 mock 数据

**Files:**
- Create: `seedmall-web/src/mock/demoData.ts`
- Create: `seedmall-web/src/api/seedmallApi.ts`

- [x] **Step 1: 实现类型、mock 数据和 API fallback**

集中定义内容、商品、AI 审核、订单结果类型，并封装 Axios 请求和降级数据。

- [x] **Step 2: 运行测试确认通过**

Run: `npm test`

Expected: PASS。

### Task 3: Vue 工作台页面

**Files:**
- Create: `seedmall-web/index.html`
- Create: `seedmall-web/src/main.ts`
- Create: `seedmall-web/src/App.vue`
- Create: `seedmall-web/src/styles.css`

- [x] **Step 1: 创建三栏工作台**

页面展示 Feed、商品秒杀和 AI 面板，并提供网关地址切换。

- [x] **Step 2: 构建验证**

Run: `npm run build`

Expected: BUILD SUCCESS。
