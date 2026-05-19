# SeedMall Infra Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 增加 SeedMall 本地 Docker Compose 基础设施和 MySQL 初始化 SQL。

**Architecture:** 根目录提供 `docker-compose.yml` 管理 Nacos、MySQL、Redis、RocketMQ；MySQL 通过挂载 `docker/mysql/init` 自动建库建表；README 作为本地启动入口文档。

**Tech Stack:** Docker Compose、MySQL 8.4、Redis 7、RocketMQ 5、Nacos standalone。

---

### Task 1: 基础设施配置

**Files:**
- Create: `.env.example`
- Create: `docker-compose.yml`

- [x] **Step 1: 创建环境变量样例**

`.env.example` 记录 MySQL、Redis、RocketMQ、Nacos 端口和账号。

- [x] **Step 2: 创建 Docker Compose**

`docker-compose.yml` 声明四个中间件服务、健康检查、数据卷和 MySQL 初始化挂载。

### Task 2: 数据库初始化

**Files:**
- Create: `docker/mysql/init/01-seedmall-schema.sql`

- [x] **Step 1: 创建业务库**

创建 `seedmall_user`、`seedmall_content`、`seedmall_product`、`seedmall_order`。

- [x] **Step 2: 创建基础表和演示数据**

创建 `user_profile`、`seed_content`、`product`、`trade_order`，并插入少量演示数据。

### Task 3: 文档与验证

**Files:**
- Modify: `README.md`

- [x] **Step 1: 更新 README**

补充 `docker compose up -d`、连接地址、初始化说明和停止命令。

- [x] **Step 2: 运行验证**

运行 `docker compose config`、`mvn test`、`npm.cmd test`、`npm.cmd run build`。
