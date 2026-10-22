-- ============================================================================
-- Debuff - CS2饰品交易平台 业务数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 说明: 包含建库、建表、外键约束、索引及初始数据
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================================
-- 1. 创建数据库
-- ============================================================================
DROP DATABASE IF EXISTS `Debuff`;
CREATE DATABASE `Debuff` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `Debuff`;

-- ============================================================================
-- 2. 删除已有表（按依赖顺序）
-- ============================================================================
DROP TABLE IF EXISTS `debuff_price_history`;
DROP TABLE IF EXISTS `debuff_announcement`;
DROP TABLE IF EXISTS `debuff_favorite`;
DROP TABLE IF EXISTS `debuff_wallet_log`;
DROP TABLE IF EXISTS `debuff_user_wallet`;
DROP TABLE IF EXISTS `debuff_buy_order`;
DROP TABLE IF EXISTS `debuff_order`;
DROP TABLE IF EXISTS `debuff_skin_listing`;
DROP TABLE IF EXISTS `debuff_inventory`;
DROP TABLE IF EXISTS `debuff_skin`;
DROP TABLE IF EXISTS `debuff_skin_quality`;
DROP TABLE IF EXISTS `debuff_weapon_type`;
DROP TABLE IF EXISTS `debuff_user`;

-- ============================================================================
-- 3. 建表语句
-- ============================================================================

-- --------------------------------------------------------------------------
-- 3.1 业务用户表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(30)  NOT NULL                COMMENT '用户名（登录账号）',
  `password`    VARCHAR(100) NOT NULL                COMMENT '密码（BCrypt加密）',
  `nickname`    VARCHAR(30)  DEFAULT NULL            COMMENT '用户昵称',
  `avatar`      VARCHAR(255) DEFAULT NULL            COMMENT '头像URL',
  `email`       VARCHAR(50)  DEFAULT NULL            COMMENT '邮箱地址',
  `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号码',
  `status`      CHAR(1)      DEFAULT '0'             COMMENT '账号状态: 0-正常 1-禁用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='业务用户表';

-- --------------------------------------------------------------------------
-- 3.2 武器类型表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_weapon_type` (
  `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '武器类型ID',
  `weapon_name`     VARCHAR(50) NOT NULL                COMMENT '武器名称',
  `weapon_category` VARCHAR(20) NOT NULL                COMMENT '武器分类: rifle/sniper/pistol/smg/shotgun/knife/glove',
  `icon`            VARCHAR(255) DEFAULT NULL           COMMENT '武器图标URL',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`weapon_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='武器类型表';

-- --------------------------------------------------------------------------
-- 3.3 饰品品质表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_skin_quality` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '品质ID',
  `quality_name` VARCHAR(50) NOT NULL                COMMENT '品质名称',
  `quality_color` VARCHAR(20) DEFAULT NULL           COMMENT '品质对应颜色（十六进制）',
  `sort_order`   INT         DEFAULT 0               COMMENT '排序权重（越小越靠前）',
  `create_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='饰品品质表';

-- --------------------------------------------------------------------------
-- 3.4 饰品定义表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_skin` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '饰品ID',
  `skin_name`      VARCHAR(100)  NOT NULL                COMMENT '饰品名称',
  `weapon_type_id` BIGINT        NOT NULL                COMMENT '武器类型ID',
  `quality_id`     BIGINT        NOT NULL                COMMENT '品质ID',
  `image_url`      VARCHAR(500)  DEFAULT NULL            COMMENT '饰品图片URL',
  `min_price`      DECIMAL(10,2) DEFAULT 0.00            COMMENT '最低参考价格',
  `status`         CHAR(1)       DEFAULT '0'             COMMENT '饰品状态: 0-正常 1-下架',
  `create_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_weapon_type` (`weapon_type_id`),
  KEY `idx_quality` (`quality_id`),
  KEY `idx_skin_name` (`skin_name`),
  CONSTRAINT `fk_skin_weapon_type` FOREIGN KEY (`weapon_type_id`) REFERENCES `debuff_weapon_type` (`id`),
  CONSTRAINT `fk_skin_quality` FOREIGN KEY (`quality_id`) REFERENCES `debuff_skin_quality` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='饰品定义表';

-- --------------------------------------------------------------------------
-- 3.5 用户库存表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_inventory` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `user_id`      BIGINT        NOT NULL                COMMENT '所属用户ID',
  `skin_id`      BIGINT        NOT NULL                COMMENT '饰品ID',
  `float_value`  DECIMAL(8,6)  DEFAULT 0.000000        COMMENT '磨损值（0~1）',
  `wear_name`    VARCHAR(20)   DEFAULT NULL            COMMENT '磨损等级名称',
  `sticker_info` JSON          DEFAULT NULL            COMMENT '贴纸信息（JSON数组）',
  `inspect_link` VARCHAR(500)  DEFAULT NULL            COMMENT '游戏内检视链接',
  `source`       VARCHAR(50)   DEFAULT NULL            COMMENT '来源: trade_up/case_drop/market_purchase',
  `status`       CHAR(1)       DEFAULT '0'             COMMENT '库存状态: 0-在库 1-在售 2-锁定',
  `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_skin_id` (`skin_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_inventory_user` FOREIGN KEY (`user_id`) REFERENCES `debuff_user` (`id`),
  CONSTRAINT `fk_inventory_skin` FOREIGN KEY (`skin_id`) REFERENCES `debuff_skin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户库存表';

-- --------------------------------------------------------------------------
-- 3.6 在售饰品表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_skin_listing` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '在售记录ID',
  `inventory_id`  BIGINT        NOT NULL                COMMENT '库存ID（唯一，一件饰品只能挂一次）',
  `seller_id`     BIGINT        NOT NULL                COMMENT '卖家用户ID',
  `skin_id`       BIGINT        NOT NULL                COMMENT '饰品ID',
  `price`         DECIMAL(10,2) NOT NULL                COMMENT '挂牌价格（元）',
  `status`        CHAR(1)       DEFAULT '0'             COMMENT '在售状态: 0-在售 1-已售 2-已取消 3-已过期',
  `listing_time`  DATETIME      DEFAULT NULL            COMMENT '上架时间',
  `sold_time`     DATETIME      DEFAULT NULL            COMMENT '成交时间',
  `expire_time`   DATETIME      DEFAULT NULL            COMMENT '过期时间',
  `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_id` (`inventory_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_skin_id` (`skin_id`),
  KEY `idx_price` (`price`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_listing_inventory` FOREIGN KEY (`inventory_id`) REFERENCES `debuff_inventory` (`id`),
  CONSTRAINT `fk_listing_seller` FOREIGN KEY (`seller_id`) REFERENCES `debuff_user` (`id`),
  CONSTRAINT `fk_listing_skin` FOREIGN KEY (`skin_id`) REFERENCES `debuff_skin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='在售饰品表';

-- --------------------------------------------------------------------------
-- 3.7 交易订单表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_order` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`      VARCHAR(32)   NOT NULL                COMMENT '订单编号（业务唯一）',
  `skin_id`       BIGINT        NOT NULL                COMMENT '饰品ID',
  `inventory_id`  BIGINT        NOT NULL                COMMENT '库存ID',
  `listing_id`    BIGINT        NOT NULL                COMMENT '在售记录ID',
  `buyer_id`      BIGINT        NOT NULL                COMMENT '买家用户ID',
  `seller_id`     BIGINT        NOT NULL                COMMENT '卖家用户ID',
  `price`         DECIMAL(10,2) NOT NULL                COMMENT '成交价格（元）',
  `platform_fee`  DECIMAL(10,2) DEFAULT 0.00            COMMENT '平台手续费（元）',
  `seller_income` DECIMAL(10,2) NOT NULL                COMMENT '卖家实际收入（元）',
  `status`        CHAR(1)       DEFAULT '0'             COMMENT '订单状态: 0-待付款 1-已付款 2-已发货 3-已完成 4-已取消 5-退款中 6-已退款',
  `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_skin_id` (`skin_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `debuff_user` (`id`),
  CONSTRAINT `fk_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `debuff_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易订单表';

-- --------------------------------------------------------------------------
-- 3.8 求购单表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_buy_order` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '求购单ID',
  `user_id`     BIGINT        NOT NULL                COMMENT '求购用户ID',
  `skin_id`     BIGINT        NOT NULL                COMMENT '目标饰品ID',
  `price`       DECIMAL(10,2) NOT NULL                COMMENT '求购价格（元）',
  `status`      CHAR(1)       DEFAULT '0'             COMMENT '求购状态: 0-求购中 1-已成交 2-已取消 3-已过期',
  `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_skin_id` (`skin_id`),
  KEY `idx_status` (`status`),
  KEY `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='求购单表';

-- --------------------------------------------------------------------------
-- 3.9 用户钱包表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_user_wallet` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
  `user_id`         BIGINT        NOT NULL                COMMENT '用户ID',
  `balance`         DECIMAL(12,2) DEFAULT 0.00            COMMENT '可用余额（元）',
  `frozen_amount`   DECIMAL(12,2) DEFAULT 0.00            COMMENT '冻结金额（元）',
  `total_recharge`  DECIMAL(12,2) DEFAULT 0.00            COMMENT '累计充值（元）',
  `total_withdraw`  DECIMAL(12,2) DEFAULT 0.00            COMMENT '累计提现（元）',
  `total_income`    DECIMAL(12,2) DEFAULT 0.00            COMMENT '累计收入（元）',
  `total_expense`   DECIMAL(12,2) DEFAULT 0.00            COMMENT '累计支出（元）',
  `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户钱包表';

-- --------------------------------------------------------------------------
-- 3.10 钱包流水表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_wallet_log` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `user_id`         BIGINT        NOT NULL                COMMENT '用户ID',
  `wallet_id`       BIGINT        NOT NULL                COMMENT '钱包ID',
  `type`            INT           NOT NULL                COMMENT '流水类型: 1-充值 2-提现 3-买入 4-卖出 5-退款 6-手续费',
  `amount`          DECIMAL(12,2) NOT NULL                COMMENT '变动金额（正数为收入，负数为支出）',
  `balance_before`  DECIMAL(12,2) DEFAULT NULL            COMMENT '变动前余额',
  `balance_after`   DECIMAL(12,2) DEFAULT NULL            COMMENT '变动后余额',
  `related_order_no` VARCHAR(32)  DEFAULT NULL            COMMENT '关联订单编号',
  `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_wallet_id` (`wallet_id`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='钱包流水表';

-- --------------------------------------------------------------------------
-- 3.11 收藏表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id`     BIGINT   NOT NULL                COMMENT '用户ID',
  `listing_id`  BIGINT   NOT NULL                COMMENT '在售记录ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_listing` (`user_id`, `listing_id`),
  KEY `idx_listing_id` (`listing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏表';

-- --------------------------------------------------------------------------
-- 3.12 公告表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_announcement` (
  `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title`             VARCHAR(100) NOT NULL                COMMENT '公告标题',
  `content`           TEXT         DEFAULT NULL            COMMENT '公告内容',
  `announcement_type` CHAR(1)      DEFAULT '0'             COMMENT '公告类型: 0-系统公告 1-活动公告 2-维护公告',
  `status`            CHAR(1)      DEFAULT '0'             COMMENT '状态: 0-已发布 1-草稿 2-已下架',
  `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`announcement_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告表';

-- --------------------------------------------------------------------------
-- 3.13 价格历史表
-- --------------------------------------------------------------------------
CREATE TABLE `debuff_price_history` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `skin_id`     BIGINT        NOT NULL                COMMENT '饰品ID',
  `avg_price`   DECIMAL(10,2) DEFAULT NULL            COMMENT '日均价（元）',
  `min_price`   DECIMAL(10,2) DEFAULT NULL            COMMENT '日最低价（元）',
  `max_price`   DECIMAL(10,2) DEFAULT NULL            COMMENT '日最高价（元）',
  `volume`      INT           DEFAULT 0               COMMENT '日成交量',
  `record_date` DATE          NOT NULL                COMMENT '记录日期',
  PRIMARY KEY (`id`),
  KEY `idx_skin_id` (`skin_id`),
  KEY `idx_record_date` (`record_date`),
  KEY `idx_skin_date` (`skin_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='价格历史表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- 4. 初始数据
-- ============================================================================

-- --------------------------------------------------------------------------
-- 4.1 武器类型数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_weapon_type` (`weapon_name`, `weapon_category`) VALUES
('AK-47',          'rifle'),
('M4A4',           'rifle'),
('AWP',            'sniper'),
('Desert Eagle',   'pistol'),
('USP-S',          'pistol'),
('Glock-18',       'pistol'),
('P90',            'smg'),
('MP9',            'smg'),
('Nova',           'shotgun'),
('Mag-7',          'shotgun'),
('Karambit',       'knife'),
('Butterfly Knife','knife'),
('Sport Gloves',   'glove'),
('Driver Gloves',  'glove');

-- --------------------------------------------------------------------------
-- 4.2 饰品品质数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_skin_quality` (`quality_name`, `quality_color`, `sort_order`) VALUES
('Battle-Scarred', '#514316', 1),
('Well-Minished',  '#3b5d16', 2),
('Field-Tested',   '#865d13', 3),
('Minimal Wear',   '#2e8b57', 4),
('Factory New',    '#4b69ff', 5),
('Souvenir',       '#ffd700', 6),
('Stattrak',       '#cf6a32', 7);

-- --------------------------------------------------------------------------
-- 4.3 测试用户数据（密码均为 admin123，BCrypt加密）
-- --------------------------------------------------------------------------
INSERT INTO `debuff_user` (`username`, `password`, `nickname`, `email`, `phone`, `status`) VALUES
('testuser1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '测试用户一', 'test1@debuff.com', '13800000001', '0'),
('testuser2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '测试用户二', 'test2@debuff.com', '13800000002', '0');

-- --------------------------------------------------------------------------
-- 4.4 示例饰品数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_skin` (`skin_name`, `weapon_type_id`, `quality_id`, `image_url`, `min_price`, `status`) VALUES
('AK-47 | Redline',           1,  3, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_ak47_cu_ak47_redline_light_large.png',   85.00,  '0'),
('AWP | Dragon Lore',         3,  5, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_awp_cu_medusa_light_large.png',         15000.00, '0'),
('M4A4 | Howl',               2,  3, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_m4a1_cu_m4a1_howl_light_large.png',     3200.00, '0'),
('AK-47 | Vulcan',            1,  5, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_ak47_cu_ak47_vulcan_light_large.png',   320.00, '0'),
('AWP | Asiimov',             3,  4, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_awp_cu_awp_asimov_light_large.png',     180.00, '0'),
('Desert Eagle | Blaze',      4,  5, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_deagle_aa_flames_light_large.png',      450.00, '0'),
('M4A4 | Neo-Noir',           2,  4, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_m4a1_cu_m4a4_neo_noir_light_large.png', 95.00,  '0'),
('USP-S | Kill Confirmed',    5,  3, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_usp_s_cu_usp_kill_confirmed_light_large.png', 280.00, '0'),
('Karambit | Fade',          11,  5, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_knife_karambit_aa_fade_light_large.png', 2800.00, '0'),
('Butterfly Knife | Doppler',12,  5, 'https://steamcdn-a.akamaihd.net/apps/730/icons/econ/default_generated/weapon_knife_butterfly_cu_knife_butterfly_doppler_light_large.png', 2200.00, '0');

-- --------------------------------------------------------------------------
-- 4.5 用户钱包数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_user_wallet` (`user_id`, `balance`, `frozen_amount`, `total_recharge`, `total_withdraw`, `total_income`, `total_expense`) VALUES
(1, 5000.00,  200.00, 6000.00, 800.00,  500.00,  700.00),
(2, 12000.00, 0.00,   15000.00, 3000.00, 2000.00, 2000.00);

-- --------------------------------------------------------------------------
-- 4.6 示例库存数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_inventory` (`user_id`, `skin_id`, `float_value`, `wear_name`, `source`, `status`) VALUES
(1, 1,  0.185432, 'Field-Tested',    'market_purchase', '0'),
(1, 6,  0.008721, 'Factory New',     'case_drop',       '0'),
(2, 3,  0.210045, 'Field-Tested',    'trade_up',        '0'),
(2, 9,  0.003215, 'Factory New',     'market_purchase', '0'),
(2, 10, 0.062834, 'Minimal Wear',    'case_drop',       '0');

-- --------------------------------------------------------------------------
-- 4.7 示例在售数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_skin_listing` (`inventory_id`, `seller_id`, `skin_id`, `price`, `status`, `listing_time`, `expire_time`) VALUES
(1, 1, 1, 92.00,  '0', '2025-01-10 10:00:00', '2025-02-10 10:00:00'),
(2, 1, 6, 468.00, '0', '2025-01-12 14:30:00', '2025-02-12 14:30:00'),
(3, 2, 3, 3350.00,'0', '2025-01-08 09:00:00', '2025-02-08 09:00:00');

-- --------------------------------------------------------------------------
-- 4.8 示例订单数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_order` (`order_no`, `skin_id`, `inventory_id`, `listing_id`, `buyer_id`, `seller_id`, `price`, `platform_fee`, `seller_income`, `status`) VALUES
('DB20250105000001', 4, 100, 50, 2, 1, 330.00, 9.90, 320.10, '3'),
('DB20250108000002', 7, 101, 51, 1, 2, 98.00,  2.94, 95.06,  '1');

-- --------------------------------------------------------------------------
-- 4.9 示例钱包流水数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_wallet_log` (`user_id`, `wallet_id`, `type`, `amount`, `balance_before`, `balance_after`, `related_order_no`) VALUES
(1, 1, 1,  5000.00, 0.00,    5000.00, NULL),
(1, 1, 6,  -9.90,   5200.00, 5190.10, 'DB20250105000001'),
(1, 1, 3,  -330.00, 5190.10, 4860.10, 'DB20250105000001'),
(2, 2, 1,  15000.00, 0.00,   15000.00, NULL),
(2, 2, 4,   320.10, 12200.00, 12520.10, 'DB20250105000001'),
(2, 2, 2,  -3000.00, 12520.10, 9520.10, NULL);

-- --------------------------------------------------------------------------
-- 4.10 示例公告数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_announcement` (`title`, `content`, `announcement_type`, `status`) VALUES
('Debuff平台正式上线', '欢迎来到Debuff，一个安全、透明的CS2饰品交易平台。注册即送新人礼包！', '0', '0'),
('交易手续费调整通知', '自2025年2月1日起，平台交易手续费由3%调整为2.5%，欢迎广大用户监督。', '0', '0'),
('春节活动：交易翻倍积分', '2025年1月25日至2月10日期间，所有交易积分翻倍，积分可兑换精美头像框。', '1', '0');

-- --------------------------------------------------------------------------
-- 4.11 示例价格历史数据
-- --------------------------------------------------------------------------
INSERT INTO `debuff_price_history` (`skin_id`, `avg_price`, `min_price`, `max_price`, `volume`, `record_date`) VALUES
(1, 88.50,  82.00,  95.00,  120, '2025-01-10'),
(1, 89.20,  83.50,  96.00,  135, '2025-01-11'),
(1, 90.10,  85.00,  97.50,  110, '2025-01-12'),
(2, 14800.00, 14500.00, 15200.00, 5,  '2025-01-10'),
(2, 14950.00, 14600.00, 15300.00, 8,  '2025-01-11'),
(2, 15100.00, 14800.00, 15500.00, 3,  '2025-01-12'),
(3, 3150.00, 3050.00, 3280.00, 15, '2025-01-10'),
(3, 3180.00, 3100.00, 3300.00, 12, '2025-01-11'),
(3, 3220.00, 3150.00, 3350.00, 18, '2025-01-12');

-- ============================================================================
-- 脚本执行完毕
-- ============================================================================
