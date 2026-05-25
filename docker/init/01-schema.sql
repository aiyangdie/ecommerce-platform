-- 电商核心表（MVP）— 与 mall-api 模块对应
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    mobile      VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    nickname    VARCHAR(64) DEFAULT NULL,
    avatar      VARCHAR(512) DEFAULT NULL,
    status      TINYINT NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
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
    sku_name    VARCHAR(128) NOT NULL COMMENT '规格描述',
    price       DECIMAL(12,2) NOT NULL,
    stock       INT NOT NULL DEFAULT 0,
    version     INT NOT NULL DEFAULT 0 COMMENT '乐观锁',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_spu (spu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU';

CREATE TABLE IF NOT EXISTS mall_order (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no        VARCHAR(32) NOT NULL UNIQUE,
    user_id         BIGINT NOT NULL,
    total_amount    DECIMAL(12,2) NOT NULL,
    pay_amount      DECIMAL(12,2) NOT NULL,
    status          TINYINT NOT NULL DEFAULT 10 COMMENT '10待支付 20待发货 30待收货 40已完成 50已取消',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

INSERT INTO product_spu (id, title, sub_title, cover_url, status) VALUES
(1, '示例商品 · 高品质电商演示', '开源 Spring Boot + uni-app 全栈', NULL, 1);

INSERT INTO product_sku (spu_id, sku_name, price, stock) VALUES
(1, '默认规格', 99.00, 999);
