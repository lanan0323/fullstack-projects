-- =============================================
-- Debuff 饰品交易业务表初始化脚本
-- 执行顺序：在 ry_20231130.sql 和 quartz.sql 之后执行
-- 数据库：Debuff
-- =============================================

-- ----------------------------
-- 1. 武器类型表
-- ----------------------------
DROP TABLE IF EXISTS debuff_weapon_type;
CREATE TABLE debuff_weapon_type (
  id          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键',
  weapon_name VARCHAR(64)  NOT NULL                 COMMENT '武器名称',
  weapon_category VARCHAR(32) NOT NULL              COMMENT '武器分类(rifle/pistol/smg/sniper/shotgun/knife/glove)',
  icon        VARCHAR(256) DEFAULT ''               COMMENT '武器图标URL',
  sort_order  INT          DEFAULT 0                COMMENT '排序号',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='武器类型表';

-- ----------------------------
-- 2. 饰品品质表
-- ----------------------------
DROP TABLE IF EXISTS debuff_skin_quality;
CREATE TABLE debuff_skin_quality (
  id           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键',
  quality_name VARCHAR(32)  NOT NULL                 COMMENT '品质名称',
  quality_color VARCHAR(16) DEFAULT '#B0C3D9'        COMMENT '品质颜色(十六进制)',
  sort_order   INT          DEFAULT 0                COMMENT '排序号',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饰品品质表';

-- ----------------------------
-- 3. 饰品定义表
-- ----------------------------
DROP TABLE IF EXISTS debuff_skin;
CREATE TABLE debuff_skin (
  id             BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
  skin_name      VARCHAR(128)  NOT NULL                 COMMENT '饰品名称',
  weapon_type_id BIGINT        NOT NULL                 COMMENT '关联武器类型ID',
  quality_id     BIGINT        NOT NULL                 COMMENT '关联品质ID',
  image_url      VARCHAR(512)  DEFAULT ''               COMMENT '饰品图片URL',
  description    TEXT                                   COMMENT '饰品描述',
  min_price      DECIMAL(10,2) DEFAULT 0.00             COMMENT '建议最低价格',
  status         CHAR(1)       DEFAULT '0'              COMMENT '状态(0上架 1下架)',
  create_time    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_weapon_type (weapon_type_id),
  KEY idx_quality (quality_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饰品定义表';

-- ----------------------------
-- 4. 用户库存表（饰品实例）
-- ----------------------------
DROP TABLE IF EXISTS debuff_inventory;
CREATE TABLE debuff_inventory (
  id           BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
  user_id      BIGINT        NOT NULL                 COMMENT '所属用户ID',
  skin_id      BIGINT        NOT NULL                 COMMENT '关联饰品定义ID',
  float_value  DECIMAL(8,6)  DEFAULT 0.000000         COMMENT '磨损度数值(0.00~1.00)',
  wear_name    VARCHAR(32)   DEFAULT ''               COMMENT '磨损名称(自动生成)',
  sticker_info VARCHAR(512)  DEFAULT ''               COMMENT '贴纸信息(JSON格式)',
  inspect_link VARCHAR(512)  DEFAULT ''               COMMENT '游戏内检视链接',
  source       VARCHAR(32)   DEFAULT 'upload'         COMMENT '来源(purchase/upload/trade)',
  status       TINYINT       DEFAULT 0                COMMENT '状态(0在库 1在售 2冻结)',
  create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
  update_time  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_skin_id (skin_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户库存表';

-- ----------------------------
-- 5. 在售饰品表
-- ----------------------------
DROP TABLE IF EXISTS debuff_skin_listing;
CREATE TABLE debuff_skin_listing (
  id           BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
  inventory_id BIGINT        NOT NULL                 COMMENT '关联库存ID',
  seller_id    BIGINT        NOT NULL                 COMMENT '卖家用户ID',
  skin_id      BIGINT        NOT NULL                 COMMENT '关联饰品定义ID(冗余)',
  price        DECIMAL(10,2) NOT NULL                 COMMENT '售价',
  status       TINYINT       DEFAULT 0                COMMENT '状态(0在售 1已售 2已下架 3已取消)',
  listing_time DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
  sold_time    DATETIME      DEFAULT NULL             COMMENT '售出时间',
  expire_time  DATETIME      DEFAULT NULL             COMMENT '自动下架时间',
  PRIMARY KEY (id),
  KEY idx_seller_id (seller_id),
  KEY idx_skin_id (skin_id),
  KEY idx_status (status),
  KEY idx_inventory_id (inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在售饰品表';

-- ----------------------------
-- 6. 交易订单表
-- ----------------------------
DROP TABLE IF EXISTS debuff_order;
CREATE TABLE debuff_order (
  id            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
  order_no      VARCHAR(32)   NOT NULL DEFAULT ''      COMMENT '订单编号(唯一)',
  skin_id       BIGINT        NOT NULL                 COMMENT '关联饰品定义ID',
  inventory_id  BIGINT        DEFAULT NULL             COMMENT '关联库存ID(下单快照)',
  listing_id    BIGINT        DEFAULT NULL             COMMENT '关联上架ID(下单快照)',
  buyer_id      BIGINT        NOT NULL                 COMMENT '买家用户ID',
  seller_id     BIGINT        NOT NULL                 COMMENT '卖家用户ID',
  price         DECIMAL(10,2) NOT NULL                 COMMENT '成交价格',
  platform_fee  DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '平台手续费',
  seller_income DECIMAL(10,2) NOT NULL DEFAULT 0.00    COMMENT '卖家实收',
  status        TINYINT       NOT NULL DEFAULT 0        COMMENT '状态(0待确认 1已完成 2已取消)',
  pay_time      DATETIME      DEFAULT NULL              COMMENT '确认时间',
  cancel_time   DATETIME      DEFAULT NULL              COMMENT '取消时间',
  create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_buyer_id (buyer_id),
  KEY idx_seller_id (seller_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易订单表';

-- ----------------------------
-- 7. 用户钱包表
-- ----------------------------
DROP TABLE IF EXISTS debuff_user_wallet;
CREATE TABLE debuff_user_wallet (
  id             BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
  user_id        BIGINT        NOT NULL                 COMMENT '用户ID',
  balance        DECIMAL(12,2) DEFAULT 0.00             COMMENT '可用余额',
  frozen_amount  DECIMAL(12,2) DEFAULT 0.00             COMMENT '冻结金额',
  total_recharge DECIMAL(12,2) DEFAULT 0.00             COMMENT '累计充值',
  total_withdraw DECIMAL(12,2) DEFAULT 0.00             COMMENT '累计提现',
  total_income   DECIMAL(12,2) DEFAULT 0.00             COMMENT '累计收入',
  total_expense  DECIMAL(12,2) DEFAULT 0.00             COMMENT '累计支出',
  create_time    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表';

-- ----------------------------
-- 8. 钱包流水表
-- ----------------------------
DROP TABLE IF EXISTS debuff_wallet_log;
CREATE TABLE debuff_wallet_log (
  id              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
  user_id         BIGINT        NOT NULL                 COMMENT '用户ID',
  wallet_id       BIGINT        NOT NULL                 COMMENT '钱包ID',
  type            TINYINT       NOT NULL                 COMMENT '类型(1充值 2提现 3买入支出 4卖出收入 5退款 6手续费)',
  amount          DECIMAL(12,2) NOT NULL                 COMMENT '金额(正收入 负支出)',
  balance_before  DECIMAL(12,2) DEFAULT 0.00             COMMENT '变动前余额',
  balance_after   DECIMAL(12,2) DEFAULT 0.00             COMMENT '变动后余额',
  related_order_no VARCHAR(64)  DEFAULT ''               COMMENT '关联订单号',
  remark          VARCHAR(256)  DEFAULT ''               COMMENT '备注',
  create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_wallet_id (wallet_id),
  KEY idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包流水表';

-- ----------------------------
-- 9. 收藏表
-- ----------------------------
DROP TABLE IF EXISTS debuff_favorite;
CREATE TABLE debuff_favorite (
  id          BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '主键',
  user_id     BIGINT   NOT NULL                 COMMENT '用户ID',
  listing_id  BIGINT   NOT NULL                 COMMENT '在售饰品ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_listing (user_id, listing_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ----------------------------
-- 10. 公告表
-- ----------------------------
DROP TABLE IF EXISTS debuff_announcement;
CREATE TABLE debuff_announcement (
  id                BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键',
  title             VARCHAR(128) NOT NULL                 COMMENT '公告标题',
  content           TEXT                                  COMMENT '公告内容',
  announcement_type CHAR(1)      DEFAULT '1'              COMMENT '类型(0通知 1公告)',
  status            CHAR(1)      DEFAULT '0'              COMMENT '状态(0正常 1关闭)',
  create_by         VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  remark            VARCHAR(256) DEFAULT ''               COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- ----------------------------
-- 11. 价格历史表
-- ----------------------------
DROP TABLE IF EXISTS debuff_price_history;
CREATE TABLE debuff_price_history (
  id          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
  skin_id     BIGINT        NOT NULL                 COMMENT '饰品定义ID',
  avg_price   DECIMAL(10,2) DEFAULT 0.00             COMMENT '当日均价',
  min_price   DECIMAL(10,2) DEFAULT 0.00             COMMENT '当日最低价',
  max_price   DECIMAL(10,2) DEFAULT 0.00             COMMENT '当日最高价',
  volume      INT           DEFAULT 0                COMMENT '当日成交量',
  record_date DATE          NOT NULL                 COMMENT '记录日期',
  PRIMARY KEY (id),
  KEY idx_skin_date (skin_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格历史表';

-- ----------------------------
-- 12. sys_user 扩展字段
-- ----------------------------
ALTER TABLE sys_user ADD COLUMN steam_id VARCHAR(64) DEFAULT '' COMMENT 'Steam64位ID' AFTER nick_name;
ALTER TABLE sys_user ADD COLUMN avatar_url VARCHAR(256) DEFAULT '' COMMENT '头像URL' AFTER steam_id;
