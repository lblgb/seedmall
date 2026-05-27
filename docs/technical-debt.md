# SeedMall 技术债与后续补强点

这份文档记录当前为了学习节奏先简化处理、后续需要补强的设计点。

## 秒杀 Reservation Key 生命周期

当前 `seedmall-seckill` 使用 Redis key 防止同一用户重复抢同一商品：

```text
seckill:reservation:{productId}:{userId}
```

现状：

- 首次秒杀请求通过 `SETNX` 写入 reservation key。
- key TTL 当前为 30 分钟。
- 重复请求命中 key 后直接返回“已在排队中”，不重复扣库存、不重复发 MQ。
- 库存不足时会主动删除 reservation key，允许用户后续重试。

当前简化点：

- 抢购成功后 reservation key 只依赖 30 分钟 TTL 自动过期。
- 订单创建失败、订单取消、活动结束等状态还没有反向释放或延长 reservation key。
- 如果业务要求“每人每活动只允许买一次”，TTL 应该跟活动结束时间对齐，不能固定 30 分钟。

后续补强方向：

- 订单消费失败时释放 reservation key。
- 订单超时取消时按业务规则决定是否释放 reservation key。
- 秒杀活动结束后统一清理活动相关 reservation key。
- 将固定 TTL 改为活动结束时间或活动配置中的防重复窗口。
