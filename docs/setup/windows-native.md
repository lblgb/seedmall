# SeedMall Windows 原生启动指南

## 目标

这份文档用于在不依赖 Docker Desktop 的 Windows 环境中启动 SeedMall 所需中间件和应用。后续切到 Linux/Docker 时，端口、库名和环境变量保持一致。

## 已具备环境

- JDK 21：`java -version`
- Maven 3.9+：`mvn -version`
- Node.js 20+：`node -v`
- npm：`npm.cmd -v`

## 中间件版本建议

- MySQL 8.x
- Redis 6+ 或 7.x
- RocketMQ 5.x
- Nacos 2.3.x

## 端口约定

| 组件 | 地址 |
| --- | --- |
| Nacos | `127.0.0.1:8848` |
| MySQL | `127.0.0.1:3306` |
| Redis | `127.0.0.1:6379` |
| RocketMQ NameServer | `127.0.0.1:9876` |
| RocketMQ Broker | `127.0.0.1:10911` |
| 后端网关 | `http://127.0.0.1:9000` |
| 前端 | `http://127.0.0.1:5173` |

## 环境变量

默认配置已经能连接本机中间件。如果你修改了端口或密码，可以在 PowerShell 中设置：

```powershell
$env:SEEDMALL_NACOS_ADDR="127.0.0.1:8848"
$env:SEEDMALL_MYSQL_HOST="127.0.0.1"
$env:SEEDMALL_MYSQL_PORT="3306"
$env:SEEDMALL_MYSQL_USERNAME="root"
$env:SEEDMALL_MYSQL_PASSWORD="root"
$env:SEEDMALL_REDIS_HOST="127.0.0.1"
$env:SEEDMALL_REDIS_PORT="6379"
$env:SEEDMALL_ROCKETMQ_NAME_SERVER="127.0.0.1:9876"
```

AI 密钥按需设置：

```powershell
$env:SEEDMALL_AI_API_KEY="你的 OpenAI 兼容 API Key"
```

## MySQL 初始化

登录 MySQL 后执行初始化脚本：

```powershell
mysql -uroot -proot < docker/mysql/init/01-seedmall-schema.sql
```

执行后会创建：

- `seedmall_user`
- `seedmall_content`
- `seedmall_product`
- `seedmall_order`

## Redis 初始化秒杀库存

秒杀服务会读取 Redis 中的库存 key。启动 Redis 后执行：

```powershell
redis-cli SET seckill:stock:101 36
redis-cli SET seckill:stock:102 58
redis-cli SET seckill:stock:103 24
```

## RocketMQ 主题

本地开发建议开启 RocketMQ 自动创建 topic，项目会使用：

- `seedmall-content-audit-topic`
- `seedmall-order-create-topic`

如果 RocketMQ 解压在 `E:\tools\rocketmq`，可以分别启动：

```powershell
E:\tools\rocketmq\bin\mqnamesrv.cmd
E:\tools\rocketmq\bin\mqbroker.cmd -c E:\tools\rocketmq\conf\broker-local.conf
```

## 启动顺序

1. 启动 MySQL，并执行初始化 SQL。
2. 启动 Redis，并写入秒杀库存 key。
3. 启动 RocketMQ NameServer 和 Broker。
4. 启动 Nacos standalone。
5. 在项目根目录执行后端验证：

```powershell
mvn test
```

6. 启动后端服务，建议顺序：

```powershell
mvn -pl seedmall-auth spring-boot:run
mvn -pl seedmall-user spring-boot:run
mvn -pl seedmall-content spring-boot:run
mvn -pl seedmall-product spring-boot:run
mvn -pl seedmall-seckill spring-boot:run
mvn -pl seedmall-order spring-boot:run
mvn -pl seedmall-ai spring-boot:run
mvn -pl seedmall-gateway spring-boot:run
```

7. 启动前端：

```powershell
cd seedmall-web
npm.cmd install
npm.cmd run dev
```

8. 浏览器访问：

```text
http://127.0.0.1:5173
```

## 常见问题

### Nacos 注册不上

确认 Nacos 控制台能打开：

```text
http://127.0.0.1:8848/nacos
```

如果端口不是 `8848`，设置 `SEEDMALL_NACOS_ADDR`。

### MySQL 连接失败

确认库已创建，账号密码与环境变量一致：

```powershell
mysql -uroot -proot -e "SHOW DATABASES;"
```

### 秒杀提示库存不足

确认 Redis key 存在：

```powershell
redis-cli GET seckill:stock:101
```

### 前端显示演示模式

这不是错误。后端网关不可用时，前端会自动降级到 mock 数据。启动 `seedmall-gateway` 后再刷新即可。
