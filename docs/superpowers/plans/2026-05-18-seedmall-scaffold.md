# SeedMall Scaffold Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建 SeedMall Spring Cloud 微服务学习项目骨架。

**Architecture:** 使用 Maven 多模块组织服务，公共模块提供响应和异常，API 模块提供 Feign/DTO，业务服务各自独立启动。网关统一路由，秒杀服务用 Redis + MQ 表达大流量入口，AI 服务封装 OpenAI 兼容接口。

**Tech Stack:** Java 21、Spring Boot 3.2.5、Spring Cloud 2023.0.1、Spring Cloud Alibaba 2023.0.1.0、MyBatis-Plus 3.5.7、Redis、RabbitMQ、Nacos、Sentinel。

---

### Task 1: 初始化工程与规范

**Files:**
- Create: `pom.xml`
- Create: `.editorconfig`
- Create: `.gitignore`
- Create: `docs/superpowers/specs/2026-05-18-seedmall-design.md`

- [x] **Step 1: 创建父级 Maven 工程**

父级工程统一 Java 17、Spring Boot、Spring Cloud、Spring Cloud Alibaba、MyBatis-Plus 版本，并声明所有服务模块。

- [x] **Step 2: 创建编码和提交忽略规则**

`.editorconfig` 统一 UTF-8 和缩进，`.gitignore` 忽略构建产物、IDE 文件、日志和本地密钥配置。

- [x] **Step 3: 写入中文设计文档**

设计文档记录业务定位、技术约束、模块边界和八股映射。

### Task 2: 创建公共契约

**Files:**
- Create: `seedmall-common/**`
- Create: `seedmall-api/**`

- [x] **Step 1: 创建统一响应和异常处理**

`ApiResponse`、`BizException`、`GlobalExceptionHandler` 提供跨服务统一 Web 行为。

- [x] **Step 2: 创建跨服务 DTO 和 Feign 契约**

AI、订单、内容事件对象集中放在 `seedmall-api`，避免服务之间互相依赖实现模块。

### Task 3: 创建网关与业务服务骨架

**Files:**
- Create: `seedmall-gateway/**`
- Create: `seedmall-auth/**`
- Create: `seedmall-user/**`
- Create: `seedmall-content/**`
- Create: `seedmall-product/**`
- Create: `seedmall-seckill/**`
- Create: `seedmall-order/**`
- Create: `seedmall-ai/**`

- [x] **Step 1: 创建网关服务**

网关配置 Nacos 发现、服务路由和鉴权过滤器扩展点。

- [x] **Step 2: 创建认证服务**

认证服务提供演示登录接口和 JWT 签发逻辑。

- [x] **Step 3: 创建内容、商品、用户、订单实体与接口**

这些服务提供 MyBatis-Plus 实体、Mapper 和最小 Controller，方便后续补真实 CRUD。

- [x] **Step 4: 创建秒杀服务**

秒杀服务提供 Redis 预扣库存和 RabbitMQ 异步下单入口。

- [x] **Step 5: 创建 AI 服务**

AI 服务提供 OpenAI 兼容客户端和内容审核接口。

### Task 4: 验证

**Files:**
- Inspect: `**/*.java`
- Inspect: `pom.xml`

- [x] **Step 1: 运行 Maven 测试**

Run: `mvn test`

Expected: 所有模块编译和单元测试通过。

- [x] **Step 2: 检查注释约束**

确认 Java 文件头和手写方法注释存在。
