-- 开源电商系统完整 schema v1.0
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS admin_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64) NOT NULL UNIQUE,
    password    VARCHAR(128) NOT NULL COMMENT 'BCrypt',
    nickname    VARCHAR(64) DEFAULT NULL,
    status      TINYINT NOT NULL DEFAULT 1,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员';

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    mobile      VARCHAR(20) NOT NULL UNIQUE,
    nickname    VARCHAR(64) DEFAULT NULL,
    avatar      VARCHAR(512) DEFAULT NULL,
    status      TINYINT NOT NULL DEFAULT 1,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='C端用户';

CREATE TABLE IF NOT EXISTS product_spu (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(256) NOT NULL,
    sub_title   VARCHAR(512) DEFAULT NULL,
    cover_url   VARCHAR(512) DEFAULT NULL,
    category_id BIGINT DEFAULT NULL,
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '0下架 1上架',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SPU';

CREATE TABLE IF NOT EXISTS product_sku (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    spu_id      BIGINT NOT NULL,
    sku_name    VARCHAR(128) NOT NULL,
    price       DECIMAL(12,2) NOT NULL,
    stock       INT NOT NULL DEFAULT 0,
    version     INT NOT NULL DEFAULT 0,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_spu (spu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU';

CREATE TABLE IF NOT EXISTS cart_item (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    sku_id      BIGINT NOT NULL,
    quantity    INT NOT NULL DEFAULT 1,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_sku (user_id, sku_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车';

CREATE TABLE IF NOT EXISTS mall_order (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no        VARCHAR(32) NOT NULL UNIQUE,
    user_id         BIGINT NOT NULL,
    total_amount    DECIMAL(12,2) NOT NULL,
    pay_amount      DECIMAL(12,2) NOT NULL,
    status          TINYINT NOT NULL DEFAULT 10 COMMENT '10待支付 20待发货 30待收货 40已完成 50已取消',
    receiver_name   VARCHAR(64) DEFAULT NULL,
    receiver_phone  VARCHAR(20) DEFAULT NULL,
    receiver_addr   VARCHAR(512) DEFAULT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

CREATE TABLE IF NOT EXISTS order_item (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT NOT NULL,
    order_no    VARCHAR(32) NOT NULL,
    spu_id      BIGINT NOT NULL,
    sku_id      BIGINT NOT NULL,
    sku_name    VARCHAR(128) NOT NULL,
    price       DECIMAL(12,2) NOT NULL,
    quantity    INT NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order (order_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细';

-- 默认管理员由 mall-api 启动时 AdminInitRunner 创建：admin / admin123

INSERT INTO product_spu (id, title, sub_title, cover_url, status) VALUES
(1, '无线蓝牙耳机 Pro', '降噪长续航 · 开源电商演示', 'https://picsum.photos/seed/earphone/400/400', 1),
(2, '智能运动手环', '心率监测 · 睡眠分析', 'https://picsum.photos/seed/watch/400/400', 1),
(3, '便携咖啡杯', '304不锈钢 · 保温6小时', 'https://picsum.photos/seed/cup/400/400', 1);

INSERT INTO product_sku (spu_id, sku_name, price, stock) VALUES
(1, '黑色', 199.00, 100),
(1, '白色', 199.00, 80),
(2, '标准版', 299.00, 50),
(3, '350ml', 89.00, 200);
