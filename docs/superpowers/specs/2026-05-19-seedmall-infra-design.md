# SeedMall 本地基础设施设计

## 目标

为 SeedMall 增加本地开发基础设施，让 Nacos、MySQL、Redis、RabbitMQ 可以通过 Docker Compose 一键启动，并自动初始化业务数据库和基础表。

## 范围

- 新增根目录 `docker-compose.yml`。
- 新增 `.env.example`，记录可调整的端口、账号和密码。
- 新增 `docker/mysql/init/01-seedmall-schema.sql`，初始化用户、内容、商品、订单数据库和基础表。
- 更新 README，补充基础设施启动顺序。

## 技术约束

- 保持现有服务配置默认值不变：Nacos `8848`、MySQL `3306`、Redis `6379`、RabbitMQ `5672/15672`。
- MySQL root 密码默认使用 `root`，与当前服务 `application.yml` 对齐。
- SQL 文件使用 UTF-8 编码。
- 初始化表只覆盖当前骨架已经出现的实体和核心链路，不提前扩展复杂表。

## 数据库

- `seedmall_user`：`user_profile`。
- `seedmall_content`：`seed_content`。
- `seedmall_product`：`product`。
- `seedmall_order`：`trade_order`。

## 验证

- `docker compose config` 验证 Compose 文件语法。
- `mvn test` 验证 Java 工程仍可编译测试。
- `npm.cmd test` 和 `npm.cmd run build` 验证前端不受影响。
