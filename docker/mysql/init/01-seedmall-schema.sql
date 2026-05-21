-- SeedMall MySQL 初始化脚本，创建学习项目所需数据库、基础表和演示数据。
SET NAMES utf8mb4;
SET time_zone = '+08:00';

CREATE DATABASE IF NOT EXISTS seedmall_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS seedmall_content DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS seedmall_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS seedmall_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE seedmall_user;

CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户编号',
    nickname VARCHAR(64) NOT NULL COMMENT '用户昵称',
    avatar_url VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户资料表';

INSERT INTO user_profile (id, nickname, avatar_url)
VALUES
    (1, '种草体验官', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=256&q=80'),
    (2, '高并发练习生', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=256&q=80')
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname), avatar_url = VALUES(avatar_url);

USE seedmall_product;

CREATE TABLE IF NOT EXISTS product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品编号',
    name VARCHAR(128) NOT NULL COMMENT '商品名称',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '原价',
    seckill_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '秒杀价',
    tag VARCHAR(64) DEFAULT NULL COMMENT '活动标签',
    image_url VARCHAR(500) DEFAULT NULL COMMENT '商品图片',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_product_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品表';

INSERT INTO product (id, name, stock, price, seckill_price, tag, image_url)
VALUES
    (101, '轻量露营套装', 36, 399.00, 199.00, '今晚 20:00 开抢', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80'),
    (102, '便携咖啡套装', 58, 259.00, 139.00, '热榜种草', 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80'),
    (103, '桌面效率套装', 24, 699.00, 499.00, '限量补贴', 'https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=900&q=80')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    stock = VALUES(stock),
    price = VALUES(price),
    seckill_price = VALUES(seckill_price),
    tag = VALUES(tag),
    image_url = VALUES(image_url);

USE seedmall_content;

CREATE TABLE IF NOT EXISTS seed_content (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '内容编号',
    author_id BIGINT NOT NULL COMMENT '作者编号',
    product_id BIGINT DEFAULT NULL COMMENT '关联商品编号',
    title VARCHAR(128) NOT NULL COMMENT '内容标题',
    body TEXT NOT NULL COMMENT '内容正文',
    audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态：0 待审核，1 通过，2 拒绝',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_author_id (author_id),
    KEY idx_product_id (product_id),
    KEY idx_audit_status (audit_status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '种草内容表';

INSERT INTO seed_content (id, author_id, product_id, title, body, audit_status, like_count)
VALUES
    (1001, 1, 101, '周末露营轻装清单', '把帐篷、炉具、收纳和防潮垫做成一套组合，适合新手直接下单。', 1, 9820),
    (1002, 2, 102, '办公室咖啡平替方案', '用手冲壶、便携磨豆机和保温杯搭出稳定组合，适合高频通勤。', 0, 7340),
    (1003, 1, 103, '桌面效率升级', '显示器支架、机械键盘和扩展坞组合，提升远程办公效率。', 1, 6510)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    body = VALUES(body),
    audit_status = VALUES(audit_status),
    like_count = VALUES(like_count);

USE seedmall_order;

CREATE TABLE IF NOT EXISTS trade_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单编号',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户编号',
    product_id BIGINT NOT NULL COMMENT '商品编号',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0 创建，1 已支付，2 已取消',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    source VARCHAR(32) NOT NULL DEFAULT 'SECKILL' COMMENT '订单来源',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    UNIQUE KEY uk_user_product_source (user_id, product_id, source),
    KEY idx_user_id (user_id),
    KEY idx_product_id (product_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '交易订单表';
