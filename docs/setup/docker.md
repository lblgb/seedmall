# SeedMall Docker 启动指南

## 目标

这份文档用于以后切到 Linux 或 Docker Desktop 环境时启动中间件。当前 Windows 原生运行可以先看 `docs/setup/windows-native.md`。

## 启动

复制环境变量样例：

```bash
cp .env.example .env
```

启动中间件：

```bash
docker compose up -d
```

查看状态：

```bash
docker compose ps
```

停止：

```bash
docker compose down
```

重建数据库数据：

```bash
docker compose down -v
docker compose up -d
```

## 地址

- Nacos：`http://localhost:8848/nacos`
- MySQL：`localhost:3306`
- Redis：`localhost:6379`
- RabbitMQ：`http://localhost:15672`

## 迁移说明

Windows 原生和 Docker/Linux 使用同一组环境变量：

- `SEEDMALL_NACOS_ADDR`
- `SEEDMALL_MYSQL_HOST`
- `SEEDMALL_MYSQL_PORT`
- `SEEDMALL_MYSQL_USERNAME`
- `SEEDMALL_MYSQL_PASSWORD`
- `SEEDMALL_REDIS_HOST`
- `SEEDMALL_REDIS_PORT`
- `SEEDMALL_RABBITMQ_HOST`
- `SEEDMALL_RABBITMQ_PORT`
- `SEEDMALL_RABBITMQ_USERNAME`
- `SEEDMALL_RABBITMQ_PASSWORD`

因此以后切 Linux/Docker 时，通常只需要换环境变量，不需要改 Java 代码。
