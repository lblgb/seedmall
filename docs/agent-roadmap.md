# SeedMall Agent 智能体接入路线图

这份文档记录 SeedMall 项目后续接入 Agent 智能体的规划。当前优先级是先把电商主流程跑通，再让 Agent 基于真实业务能力做查询、解释和有限操作。

## 总体判断

推荐顺序：

```text
先跑通电商主流程 -> 再接入只读 Agent -> 最后扩展可执行 Agent 和 RAG
```

原因：
- Agent 的价值来自真实业务工具。商品、秒杀、订单、库存、补偿等能力越完整，Agent 越能回答真实问题。
- 如果基础流程还不稳定，Agent 容易变成单纯聊天入口，只能解释概念，不能辅助排查业务。
- 先定义文档和边界，可以避免后续接入模型时把权限、工具和业务动作混在一起。

## 当前 AI 现状

当前 `seedmall-ai` 已有能力：
- `POST /ai/audit`：内容审核入口。
- `OpenAiCompatibleClient`：OpenAI 兼容聊天接口封装。
- 未配置 `SEEDMALL_AI_API_KEY` 时，走本地规则降级。

当前还不是 Agent：
- 没有多轮会话。
- 没有工具选择。
- 没有调用商品、订单、秒杀等业务服务。
- 没有展示工具调用过程。

## 第一阶段：只读业务助手

目标：让 Agent 能回答“当前业务流程发生了什么”，但不修改任何业务数据。

建议能力：
- 查询商品信息：商品名称、库存、价格、秒杀价。
- 查询订单状态：用户是否已经生成秒杀订单、订单号、状态。
- 查询秒杀状态：Redis 秒杀库存、用户是否已有 reservation key。
- 解释流程问题：例如“为什么我点了参与秒杀没生成订单”。

接口建议：

```text
POST /ai/agent/chat
```

请求模型：

```text
AgentChatRequest
- sessionId
- userId
- message
- contextProductId
```

响应模型：

```text
AgentChatResponse
- answer
- toolCalls
- suggestions
```

工具边界：
- 第一阶段工具只读。
- 不允许创建订单。
- 不允许扣库存。
- 不允许删除 Redis key。
- 不允许执行补偿任务。

## 第二阶段：受控可执行 Agent

目标：让 Agent 在明确权限和确认机制下，执行低风险业务动作。

候选能力：
- 为运营生成内容标题和摘要。
- 为用户推荐商品。
- 帮管理员检查异常订单。
- 触发只针对测试用户或测试商品的状态刷新。
- 触发补偿任务前先给出风险提示和操作摘要。

权限约束：
- 所有写操作必须有明确工具名和参数。
- 高风险动作必须要求二次确认。
- 工具执行结果必须落日志。
- 不允许 Agent 直接拼 SQL 或直接访问数据库。

## 第三阶段：RAG 与项目知识库

目标：让 Agent 能回答项目内部问题和八股学习问题。

知识来源：
- `README.md`
- `docs/setup/*.md`
- `docs/technical-debt.md`
- `docs/agent-roadmap.md`
- 后续接口说明和架构图

典型问题：
- “秒杀链路从前端到订单落库怎么走？”
- “Redis reservation key 为什么不会永久存在？”
- “RocketMQ 在这里解决了什么问题？”
- “当前库存一致性还有哪些技术债？”

实现方向：
- 先使用文档分段和关键词检索。
- 后续再接向量库。
- 回答时尽量引用具体文档和代码路径。

## 模块设计草案

`seedmall-api`：

```text
com.seedmall.api.agent.AgentChatRequest
com.seedmall.api.agent.AgentChatResponse
com.seedmall.api.agent.AgentToolCall
```

`seedmall-ai`：

```text
controller/AgentController
service/AgentService
agent/AgentSession
agent/AgentDecision
tool/AgentTool
tool/ProductQueryTool
tool/OrderQueryTool
tool/SeckillQueryTool
```

前端：

```text
AI 助手面板
- 输入问题
- 展示回答
- 展示调用了哪些工具
- 展示建议下一步操作
```

## 推荐实现顺序

短期继续补主流程：
- 秒杀库存初始化和查询。
- 订单取消。
- Redis 预扣库存释放。
- 数据库库存回滚或补偿。
- 前端流程可视化。

并行保留 Agent 文档规划：
- 先维护本文件。
- 每补一个业务能力，就补一个可供 Agent 调用的只读工具候选。

主流程稳定后实现第一版 Agent：
- 新增 `POST /ai/agent/chat`。
- 接入商品查询工具。
- 接入订单查询工具。
- 接入秒杀状态查询工具。
- 前端展示回答和工具调用记录。

## 学习重点

Agent 接入可以覆盖这些八股和工程点：
- Prompt 约束和输出结构化。
- Function Calling / Tool Calling。
- Feign 服务调用。
- 权限边界和最小权限原则。
- 幂等和副作用控制。
- Redis 状态查询。
- MQ 异步链路解释。
- 降级策略。
- 可观测性和审计日志。

## 当前结论

现在可以先落文档，但不建议马上实现 Agent。下一步代码优先继续补电商主流程，等商品、秒杀、订单、库存一致性和前端展示更完整后，再接入第一版只读 Agent。
