# SeedMall

SeedMall 是一个 Java 微服务学习项目，业务形态是“内容种草社区 + 高并发秒杀交易 + AI 能力平台”。

## 模块

- `seedmall-gateway`：统一入口、服务路由、鉴权和限流扩展点。
- `seedmall-auth`：登录与 JWT 签发。
- `seedmall-user`：用户资料和关注关系扩展点。
- `seedmall-content`：种草内容、Feed、内容审核事件。
- `seedmall-product`：商品与库存。
- `seedmall-seckill`：秒杀入口、Redis 预扣库存、MQ 削峰。
- `seedmall-order`：订单创建和异步下单消费。
- `seedmall-ai`：OpenAI 兼容 AI 能力封装。
- `seedmall-common`：统一响应、异常和公共对象。
- `seedmall-api`：Feign 契约、DTO、事件对象。
- `seedmall-web`：Vue 3 前端工作台，用于演示内容、秒杀、AI 和订单链路。

## 本地依赖

- JDK 21
- Maven 3.9+
- Node.js 20+
- npm
- MySQL 8.x
- Redis 6+
- RocketMQ 5.x
- Nacos 2.x
- Docker Desktop 或兼容 Docker Compose 的运行环境，可选

## 启动文档

Windows 原生启动优先看：

```text
docs/setup/windows-native.md
```

Docker/Linux 启动看：

```text
docs/setup/docker.md
```

## 基础设施

复制环境变量样例：

```powershell
Copy-Item .env.example .env
```

Docker 环境下启动本地中间件：

```powershell
docker compose up -d
```

默认地址：

- Nacos 控制台：`http://localhost:8848/nacos`
- MySQL：`localhost:3306`，账号 `root`，密码 `root`
- Redis：`localhost:6379`
- RocketMQ NameServer：`localhost:9876`
- RocketMQ Broker：`localhost:10911`

MySQL 首次启动会自动执行 `docker/mysql/init/01-seedmall-schema.sql`，创建 `seedmall_user`、`seedmall_content`、`seedmall_product`、`seedmall_order` 四个数据库。

停止基础设施：

```powershell
docker compose down
```

如果需要重新初始化数据库，可以删除数据卷后再启动：

```powershell
docker compose down -v
docker compose up -d
```

## 启动顺序

1. 启动 Nacos、MySQL、Redis、RocketMQ。
2. 确认 MySQL 已初始化 `seedmall_user`、`seedmall_content`、`seedmall_product`、`seedmall_order` 数据库。
3. 按需配置 `SEEDMALL_AI_API_KEY`。
4. 执行 `mvn test` 验证项目。
5. 分别启动业务服务，再启动 `seedmall-gateway`。

## 前端预览

进入前端目录后启动 Vite：

```powershell
cd seedmall-web
npm.cmd install
npm.cmd run dev
```

默认访问地址为 `http://localhost:5173`。后端网关未启动时，前端会自动使用演示数据；后端启动后可以在页面顶部把网关地址指向 `http://localhost:9000`。

## 学习路线

1. 先读 `docs/superpowers/specs/2026-05-18-seedmall-design.md` 理解架构。
2. 从 `seedmall-common` 和 `seedmall-api` 理解公共契约。
3. 从 `seedmall-content -> seedmall-seckill -> seedmall-order` 串起内容到交易链路。
4. 在 `seedmall-ai` 中扩展审核、标题生成、客服问答。
5. 逐步补充缓存一致性、接口幂等、分布式锁、限流熔断、链路追踪和压测。

## JDK 21

项目使用 JDK 21，并在业务服务中开启 `spring.threads.virtual.enabled=true`。这个配置适合学习 AI 调用、Feign 调用、数据库访问等高延迟 IO 场景下的虚拟线程模型。
