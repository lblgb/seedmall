# Agent 前置能力收口文档

## 背景

SeedMall 目前已经具备一个基础秒杀商城链路：内容种草、商品查询、Redis 秒杀预扣、RocketMQ 异步下单、订单支付、订单取消、库存回滚。

在接入 Agent 前，需要先让交易链路具备三个特点：

1. 状态可解释：Agent 能读懂订单、库存、Redis 排队标记之间的关系。
2. 写操作单入口：Agent 未来不应该自己拼多个写接口。
3. 诊断有依据：Agent 需要事件日志，而不是只看最终状态。

## 本轮收口范围

### 统一取消编排

前端不再分别调用“取消订单”和“释放 Redis”。新的取消入口由秒杀服务统一编排：

```text
前端
  -> POST /seckill/{productId}/order/cancel
  -> SeckillService.cancelSeckillOrder
  -> OrderCommandClient.cancelSeckillOrder
  -> OrderService.cancelSeckillOrder
  -> SeckillService.releaseReservation
```

关键规则：

- 订单真的变成 `status = 2` 时，才释放 Redis reservation 并回补 Redis 秒杀库存。
- 订单已支付 `status = 1` 时，不释放 Redis，不恢复数据库库存。
- 订单不存在时，只返回当前 Redis 状态，不做写操作。

这个设计让 Agent 后续只需要调用一个取消工具，不需要自己判断多个接口的先后顺序。

### 超时未支付取消

新增超时取消能力：

```text
SeckillOrderTimeoutTask
  -> SeckillService.cancelExpiredSeckillOrders
  -> OrderCommandClient.cancelExpiredSeckillOrders
  -> OrderService.cancelExpiredSeckillOrders
  -> ProductStockClient.restoreStock
  -> SeckillService.releaseReservation
```

默认参数：

- `seedmall.seckill.timeout-minutes = 30`
- `seedmall.seckill.timeout-batch-limit = 50`
- `seedmall.seckill.timeout-scan-delay-ms = 60000`
- `seedmall.seckill.timeout-scan-initial-delay-ms = 60000`

学习重点：

- 定时任务
- 批量扫描
- 条件更新
- 库存补偿
- Redis 与数据库最终一致

### 订单事件日志

订单服务新增内存事件日志，供后续 Agent 诊断读取：

```text
GET /orders/events?userId={userId}&productId={productId}
```

当前记录事件：

- `ORDER_CREATED`
- `ORDER_REACTIVATED`
- `ORDER_PAID`
- `ORDER_CANCELED`
- `ORDER_TIMEOUT_CANCELED`

当前事件日志是内存版，适合学习和 Agent 第一阶段只读诊断。后续如果要做生产化，可以迁移到 MySQL 表或消息日志表。

## Agent 可直接复用的工具边界

### 只读工具

- 查询商品详情：`GET /products/{productId}`
- 查询 Redis 秒杀库存：`GET /seckill/{productId}/stock`
- 查询用户订单：`GET /orders/seckill`
- 查询订单事件：`GET /orders/events`

### 写操作工具

- 参与秒杀：`POST /seckill/{productId}`
- 支付订单：`POST /orders/seckill/pay`
- 统一取消秒杀订单：`POST /seckill/{productId}/order/cancel`
- 批量取消超时订单：`POST /seckill/orders/timeout-cancel`

Agent 第一阶段只开放只读工具。写操作需要等人工确认、审计日志、权限控制补齐后再开放。

## 后续 Agent 建议

### 第一阶段：只读客服 Agent

能力：

- 解释用户为什么不能重复秒杀。
- 解释订单为什么还在排队。
- 查询商品和库存状态。

### 第二阶段：订单诊断 Agent

能力：

- 识别订单状态和 Redis reservation 是否一致。
- 识别已取消订单是否仍有排队标记。
- 识别已支付订单是否被错误释放库存。

### 第三阶段：运营分析 Agent

能力：

- 分析秒杀参与、成单、支付、取消数据。
- 给出商品运营建议。
- 生成活动复盘。

### 第四阶段：可执行修复 Agent

能力：

- 在人工确认后执行统一取消、释放 reservation、补偿库存。
- 所有执行动作必须记录审计日志。

## 仍需注意

当前项目为了学习，事件日志先使用内存存储。这个实现能支撑 Agent 的第一阶段诊断学习，但服务重启后事件会丢失。后续如果 Agent 要做稳定诊断，应把事件迁移到数据库或专门的事件表。
