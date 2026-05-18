# SeedMall 微服务学习项目设计

## 目标

SeedMall 是一个“内容种草社区 + 高并发秒杀交易 + AI 能力平台”的 Java 学习项目，用来把 Spring Boot、Spring Cloud、MyBatis-Plus、Redis、MQ、大流量治理和 AI 接入这些八股知识落到可扩展的项目骨架里。

## 技术约束

- 文档和注释使用中文。
- 文件统一使用 UTF-8 编码。
- Java 版本使用 21。
- Spring Boot 服务默认开启虚拟线程，用于学习高并发 IO、AI 调用和远程服务调用场景。
- 每个 Java 代码文件开头必须有一段文件用途说明。
- 每个手写方法必须至少有一行简单 JavaDoc 或注释说明。
- MyBatis 使用 MyBatis-Plus，复杂 SQL 预留 XML 方式。
- AI 接入使用 OpenAI 兼容 HTTP API，不在仓库硬编码密钥。

## 架构

项目采用 Maven 多模块单仓结构。`seedmall-gateway` 作为统一入口，业务服务通过 Nacos 注册发现，服务间契约放在 `seedmall-api`，公共响应和异常处理放在 `seedmall-common`。

第一版以轻量骨架为主：每个服务能独立启动，包含最小接口、配置和扩展点；后续复习到具体八股点时，再逐步补齐真实数据库操作、缓存一致性、分布式事务、链路追踪和压测脚本。

## 模块

- `seedmall-common`：统一响应、业务异常、全局异常处理、分页对象。
- `seedmall-api`：Feign 契约、跨服务 DTO、MQ 事件对象。
- `seedmall-gateway`：统一路由、鉴权过滤器、限流扩展点。
- `seedmall-auth`：登录入口、JWT 签发示例。
- `seedmall-user`：用户资料和关注关系扩展点。
- `seedmall-content`：种草内容、Feed、发布审核事件。
- `seedmall-product`：商品和库存基础能力。
- `seedmall-seckill`：Redis 预扣库存、MQ 异步下单入口。
- `seedmall-order`：订单创建、异步下单消费、状态流转扩展点。
- `seedmall-ai`：OpenAI 兼容客户端、内容审核能力。

## 八股映射

- Spring Boot：自动配置、配置属性、Controller、校验、异常处理、虚拟线程配置扩展。
- Spring Cloud：Gateway、Nacos、OpenFeign、Sentinel 依赖预留。
- MyBatis-Plus：实体、Mapper、CRUD、XML 扩展点。
- Redis：热点缓存、秒杀库存、分布式锁扩展点。
- MQ：削峰填谷、异步下单、内容审核事件。
- 高并发：网关限流、秒杀预扣、幂等、缓存一致性、异步化。
- AI：统一模型供应商配置、内容审核、客服问答和标题生成扩展点。
