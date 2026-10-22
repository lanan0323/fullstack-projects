# Debuff 数据库设计文档

> **项目名称：** Debuff — CS2 饰品交易平台  
> **数据库引擎：** MySQL 8.0  
> **数据库名：** debuff  
> **字符集：** utf8mb4 / utf8mb4_general_ci  
> **文档版本：** v1.0  

---

## 1. 数据库概述

Debuff 是一个基于若依（RuoYi）框架的 CS2（Counter-Strike 2）虚拟饰品交易平台。系统支持用户注册登录、饰品库存管理、挂单出售、求购发布、订单交易、钱包充值与提现、收藏关注、公告发布以及价格历史追踪等核心业务功能。

数据库设计遵循以下原则：

- **业务表与系统表分离**：业务表以 `debuff_` 为前缀，若依系统表以 `sys_` 为前缀，两套表通过 `sys_user` 与 `debuff_user` 的关联实现权限体系与业务体系的衔接。
- **命名规范**：表名使用小写下划线风格（snake_case），字段名同样采用下划线命名，保持与 Java 实体类驼峰命名的映射一致性。
- **主键策略**：所有业务表采用 `BIGINT` 自增主键，兼顾性能与扩展性。
- **软删除与状态管理**：核心业务表通过 `status` 字段实现状态流转，避免物理删除导致的数据丢失。
- **审计字段**：所有业务表包含 `create_time` 字段记录创建时间，关键表包含 `update_time` 字段记录最后修改时间。

---

## 2. 数据库表清单

### 2.1 业务表

| 序号 | 表名 | 说明 | 所属模块 |
|:---:|------|------|---------|
| 1 | debuff_user | 业务用户表 | 用户模块 |
| 2 | debuff_weapon_type | 武器类型表 | 饰品模块 |
| 3 | debuff_skin_quality | 饰品品质表 | 饰品模块 |
| 4 | debuff_skin | 饰品定义表（SKU） | 饰品模块 |
| 5 | debuff_inventory | 用户库存表（饰品实例） | 库存模块 |
| 6 | debuff_skin_listing | 在售饰品表（挂单） | 交易模块 |
| 7 | debuff_order | 交易订单表 | 交易模块 |
| 8 | debuff_buy_order | 求购单表 | 交易模块 |
| 9 | debuff_user_wallet | 用户钱包表 | 钱包模块 |
| 10 | debuff_wallet_log | 钱包流水表 | 钱包模块 |
| 11 | debuff_favorite | 用户收藏表 | 用户模块 |
| 12 | debuff_announcement | 系统公告表 | 运营模块 |
| 13 | debuff_price_history | 价格历史表 | 数据模块 |

### 2.2 系统表（若依标准）

| 序号 | 表名 | 说明 |
|:---:|------|------|
| 1 | sys_user | 系统用户表 |
| 2 | sys_role | 角色信息表 |
| 3 | sys_menu | 菜单权限表 |
| 4 | sys_dept | 部门表 |
| 5 | sys_post | 岗位表 |
| 6 | sys_dict_type | 字典类型表 |
| 7 | sys_dict_data | 字典数据表 |
| 8 | sys_config | 参数配置表 |
| 9 | sys_notice | 系统通知表 |
| 10 | sys_oper_log | 操作日志表 |
| 11 | sys_logininfor | 登录日志表 |
| 12 | sys_job | 定时任务表 |
| 13 | sys_job_log | 定时任务日志表 |
| 14 | sys_user_role | 用户-角色关联表 |
| 15 | sys_role_menu | 角色-菜单关联表 |
| 16 | sys_role_dept | 角色-部门关联表 |
| 17 | sys_user_post | 用户-岗位关联表 |

---

## 3. 表结构详细设计

### 3.1 debuff_user — 业务用户表

存储平台注册用户的基本信息，与系统用户表 `sys_user` 通过相同用户名或扩展字段关联。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 用户唯一标识 |
| username | VARCHAR(30) | UNIQUE, NOT NULL | — | 登录用户名 |
| password | VARCHAR(100) | NOT NULL | — | 登录密码（BCrypt 加密存储） |
| nickname | VARCHAR(30) | — | — | 用户昵称/显示名 |
| avatar | VARCHAR(255) | — | NULL | 头像 URL 地址 |
| email | VARCHAR(50) | — | NULL | 电子邮箱 |
| phone | VARCHAR(20) | — | NULL | 手机号码 |
| status | CHAR(1) | NOT NULL | '0' | 账号状态：0-正常，1-停用 |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 注册时间 |
| update_time | DATETIME | — | CURRENT_TIMESTAMP ON UPDATE | 最后更新时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `UNIQUE INDEX uk_username (username)`

---

### 3.2 debuff_weapon_type — 武器类型表

定义 CS2 中所有武器分类，如步枪、手枪、冲锋枪等，作为饰品定义表的外键引用。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 武器类型唯一标识 |
| weapon_name | VARCHAR(50) | NOT NULL | — | 武器类型名称（如 AK-47、M4A4） |
| weapon_category | VARCHAR(20) | NOT NULL | — | 武器大类：rifle/pistol/smg/sniper/shotgun/knife/glove |
| icon | VARCHAR(255) | — | NULL | 武器类型图标 URL |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 创建时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `INDEX idx_category (weapon_category)`

---

### 3.3 debuff_skin_quality — 饰品品质表

定义饰品的磨损品质等级，如崭新出厂、略有磨损、久经沙场等。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 品质唯一标识 |
| quality_name | VARCHAR(50) | NOT NULL | — | 品质名称（如崭新出厂、略有磨损） |
| quality_color | VARCHAR(20) | — | NULL | 品质对应颜色值（十六进制，用于前端展示） |
| sort_order | INT | — | 0 | 排序序号，用于前端列表排序 |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 创建时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `INDEX idx_sort_order (sort_order)`

---

### 3.4 debuff_skin — 饰品定义表（SKU）

定义平台上的每一种饰品（SKU 级别），包含饰品名称、所属武器类型、品质等级、参考图片及最低价格等。此表为饰品的"模板"定义，不区分具体实例。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 饰品定义唯一标识 |
| skin_name | VARCHAR(100) | NOT NULL | — | 饰品完整名称（如 "AK-47 | 红线 (久经沙场)"） |
| weapon_type_id | BIGINT | FK, NOT NULL | — | 关联武器类型 ID |
| quality_id | BIGINT | FK, NOT NULL | — | 关联品质等级 ID |
| image_url | VARCHAR(500) | — | NULL | 饰品展示图片 URL |
| min_price | DECIMAL(10,2) | — | NULL | 当前市场最低价格（参考值） |
| status | CHAR(1) | NOT NULL | '0' | 状态：0-上架，1-下架 |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | — | CURRENT_TIMESTAMP ON UPDATE | 最后更新时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `INDEX idx_weapon_type (weapon_type_id)`
- `INDEX idx_quality (quality_id)`
- `INDEX idx_status (status)`
- `INDEX idx_skin_name (skin_name)`

**外键约束：**
- `FK_skin_weapon_type` → `debuff_weapon_type(id)`
- `FK_skin_quality` → `debuff_skin_quality(id)`

---

### 3.5 debuff_inventory — 用户库存表（饰品实例）

记录用户持有的每一件具体饰品实例。每件饰品拥有独立的磨损度（float_value）、贴纸信息及检视链接，是饰品交易的实物载体。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 库存记录唯一标识 |
| user_id | BIGINT | FK, NOT NULL | — | 所属用户 ID |
| skin_id | BIGINT | FK, NOT NULL | — | 饰品定义 ID（关联 SKU） |
| float_value | DECIMAL(8,6) | — | NULL | 磨损度数值（0.000000 ~ 1.000000） |
| wear_name | VARCHAR(20) | — | NULL | 磨损等级名称（如"崭新出厂"） |
| sticker_info | JSON | — | NULL | 贴纸信息（JSON 数组，含贴纸名称、磨损等） |
| inspect_link | VARCHAR(500) | — | NULL | CS2 游戏内检视链接 |
| source | VARCHAR(50) | — | NULL | 饰品来源（如"充值"、"交易"、"系统发放"） |
| status | CHAR(1) | NOT NULL | '0' | 状态：0-在库，1-在售，2-冻结 |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 入库时间 |
| update_time | DATETIME | — | CURRENT_TIMESTAMP ON UPDATE | 最后更新时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `INDEX idx_user_id (user_id)`
- `INDEX idx_skin_id (skin_id)`
- `INDEX idx_status (status)`

**外键约束：**
- `FK_inventory_user` → `debuff_user(id)`
- `FK_inventory_skin` → `debuff_skin(id)`

---

### 3.6 debuff_skin_listing — 在售饰品表（挂单）

记录用户发布的在售挂单信息。每条挂单对应一件库存饰品实例，包含售价、状态及有效期等。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 挂单唯一标识 |
| inventory_id | BIGINT | FK, UNIQUE, NOT NULL | — | 关联库存饰品实例 ID（一件饰品只能挂一次） |
| seller_id | BIGINT | FK, NOT NULL | — | 卖家用户 ID |
| skin_id | BIGINT | FK, NOT NULL | — | 饰品定义 ID（冗余字段，便于查询） |
| price | DECIMAL(10,2) | NOT NULL | — | 挂单售价（单位：元） |
| status | CHAR(1) | NOT NULL | '0' | 状态：0-在售，1-已售，2-已下架，3-已取消 |
| listing_time | DATETIME | — | CURRENT_TIMESTAMP | 上架时间 |
| sold_time | DATETIME | — | NULL | 成交时间 |
| expire_time | DATETIME | — | NULL | 挂单过期时间 |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | — | CURRENT_TIMESTAMP ON UPDATE | 最后更新时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `UNIQUE INDEX uk_inventory (inventory_id)`
- `INDEX idx_seller (seller_id)`
- `INDEX idx_skin (skin_id)`
- `INDEX idx_status (status)`
- `INDEX idx_price (price)`
- `INDEX idx_listing_time (listing_time)`

**外键约束：**
- `FK_listing_inventory` → `debuff_inventory(id)`
- `FK_listing_seller` → `debuff_user(id)`
- `FK_listing_skin` → `debuff_skin(id)`

---

### 3.7 debuff_order — 交易订单表

记录平台的每一笔交易订单，关联买卖双方、饰品信息及资金明细。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 订单唯一标识 |
| order_no | VARCHAR(32) | UNIQUE, NOT NULL | — | 订单编号（业务唯一） |
| skin_id | BIGINT | FK, NOT NULL | — | 饰品定义 ID |
| inventory_id | BIGINT | FK, NOT NULL | — | 饰品实例 ID |
| listing_id | BIGINT | FK, NOT NULL | — | 原挂单 ID |
| buyer_id | BIGINT | FK, NOT NULL | — | 买家用户 ID |
| seller_id | BIGINT | FK, NOT NULL | — | 卖家用户 ID |
| price | DECIMAL(10,2) | NOT NULL | — | 成交价格（单位：元） |
| platform_fee | DECIMAL(10,2) | NOT NULL | 0.00 | 平台手续费 |
| seller_income | DECIMAL(10,2) | NOT NULL | — | 卖家实际收入（price - platform_fee） |
| status | CHAR(1) | NOT NULL | '0' | 状态：0-待确认，1-已完成，2-已取消 |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 下单时间 |
| update_time | DATETIME | — | CURRENT_TIMESTAMP ON UPDATE | 最后更新时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `UNIQUE INDEX uk_order_no (order_no)`
- `INDEX idx_buyer (buyer_id)`
- `INDEX idx_seller (seller_id)`
- `INDEX idx_skin (skin_id)`
- `INDEX idx_listing (listing_id)`
- `INDEX idx_status (status)`
- `INDEX idx_create_time (create_time)`

**外键约束：**
- `FK_order_skin` → `debuff_skin(id)`
- `FK_order_inventory` → `debuff_inventory(id)`
- `FK_order_listing` → `debuff_skin_listing(id)`
- `FK_order_buyer` → `debuff_user(id)`
- `FK_order_seller` → `debuff_user(id)`

---

### 3.8 debuff_buy_order — 求购单表

记录用户发布的求购意向，买家可以设定期望价格等待卖家主动匹配。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 求购单唯一标识 |
| user_id | BIGINT | FK, NOT NULL | — | 求购用户 ID（买家） |
| skin_id | BIGINT | FK, NOT NULL | — | 目标饰品定义 ID |
| price | DECIMAL(10,2) | NOT NULL | — | 求购价格（单位：元） |
| status | CHAR(1) | NOT NULL | '0' | 状态：0-有效，1-已完成，2-已取消 |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | — | CURRENT_TIMESTAMP ON UPDATE | 最后更新时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `INDEX idx_user (user_id)`
- `INDEX idx_skin (skin_id)`
- `INDEX idx_status (status)`

**外键约束：**
- `FK_buy_order_user` → `debuff_user(id)`
- `FK_buy_order_skin` → `debuff_skin(id)`

---

### 3.9 debuff_user_wallet — 用户钱包表

记录用户的钱包余额及各类资金汇总信息，每位用户对应唯一钱包记录。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 钱包记录唯一标识 |
| user_id | BIGINT | FK, UNIQUE, NOT NULL | — | 关联用户 ID（一人一钱包） |
| balance | DECIMAL(12,2) | NOT NULL | 0.00 | 可用余额（单位：元） |
| frozen_amount | DECIMAL(12,2) | NOT NULL | 0.00 | 冻结金额（挂单/求购锁定中） |
| total_recharge | DECIMAL(12,2) | NOT NULL | 0.00 | 累计充值金额 |
| total_withdraw | DECIMAL(12,2) | NOT NULL | 0.00 | 累计提现金额 |
| total_income | DECIMAL(12,2) | NOT NULL | 0.00 | 累计收入（卖出所得） |
| total_expense | DECIMAL(12,2) | NOT NULL | 0.00 | 累计支出（买入花费） |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | — | CURRENT_TIMESTAMP ON UPDATE | 最后更新时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `UNIQUE INDEX uk_user_id (user_id)`

**外键约束：**
- `FK_wallet_user` → `debuff_user(id)`

---

### 3.10 debuff_wallet_log — 钱包流水表

记录钱包的每一笔资金变动明细，作为对账和审计的依据。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 流水记录唯一标识 |
| user_id | BIGINT | FK, NOT NULL | — | 所属用户 ID |
| wallet_id | BIGINT | FK, NOT NULL | — | 关联钱包 ID |
| type | INT | NOT NULL | — | 变动类型：1-充值，2-提现，3-买入，4-卖出，5-退款，6-手续费 |
| amount | DECIMAL(12,2) | NOT NULL | — | 变动金额（正数为收入，负数为支出） |
| balance_before | DECIMAL(12,2) | NOT NULL | — | 变动前余额 |
| balance_after | DECIMAL(12,2) | NOT NULL | — | 变动后余额 |
| related_order_no | VARCHAR(32) | — | NULL | 关联订单编号 |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 记录时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `INDEX idx_user_id (user_id)`
- `INDEX idx_wallet_id (wallet_id)`
- `INDEX idx_type (type)`
- `INDEX idx_related_order (related_order_no)`
- `INDEX idx_create_time (create_time)`

**外键约束：**
- `FK_wallet_log_user` → `debuff_user(id)`
- `FK_wallet_log_wallet` → `debuff_user_wallet(id)`

---

### 3.11 debuff_favorite — 用户收藏表

记录用户收藏的在售饰品，支持用户快速关注感兴趣的挂单。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 收藏记录唯一标识 |
| user_id | BIGINT | FK, NOT NULL | — | 收藏用户 ID |
| listing_id | BIGINT | FK, NOT NULL | — | 收藏的挂单 ID |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 收藏时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `UNIQUE INDEX uk_user_listing (user_id, listing_id)`
- `INDEX idx_listing (listing_id)`

**外键约束：**
- `FK_favorite_user` → `debuff_user(id)`
- `FK_favorite_listing` → `debuff_skin_listing(id)`

---

### 3.12 debuff_announcement — 系统公告表

管理平台发布的运营公告，支持富文本内容和公告分类。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 公告唯一标识 |
| title | VARCHAR(100) | NOT NULL | — | 公告标题 |
| content | TEXT | — | NULL | 公告内容（富文本 HTML） |
| announcement_type | CHAR(1) | — | '0' | 公告类型（如 0-系统公告，1-活动公告，2-维护公告） |
| status | CHAR(1) | NOT NULL | '0' | 状态：0-正常显示，1-已关闭 |
| create_time | DATETIME | — | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | — | CURRENT_TIMESTAMP ON UPDATE | 最后更新时间 |

**索引设计：**
- `PRIMARY KEY (id)`
- `INDEX idx_status (status)`
- `INDEX idx_type (announcement_type)`

---

### 3.13 debuff_price_history — 价格历史表

按日记录每种饰品的市场价格走势数据，用于前端价格图表展示和市场分析。

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|---------|------|--------|------|
| id | BIGINT | PK, AUTO_INCREMENT | — | 记录唯一标识 |
| skin_id | BIGINT | FK, NOT NULL | — | 饰品定义 ID |
| avg_price | DECIMAL(10,2) | — | NULL | 当日成交均价 |
| min_price | DECIMAL(10,2) | — | NULL | 当日最低成交价 |
| max_price | DECIMAL(10,2) | — | NULL | 当日最高成交价 |
| volume | INT | — | 0 | 当日成交量 |
| record_date | DATE | NOT NULL | — | 数据日期 |

**索引设计：**
- `PRIMARY KEY (id)`
- `UNIQUE INDEX uk_skin_date (skin_id, record_date)`
- `INDEX idx_skin (skin_id)`
- `INDEX idx_record_date (record_date)`

**外键约束：**
- `FK_price_history_skin` → `debuff_skin(id)`

---

## 4. ER 关系分析

### 4.1 实体关系总览

系统核心实体包括：用户（User）、饰品定义（Skin）、饰品实例（Inventory）、挂单（Listing）、订单（Order）、求购单（BuyOrder）、钱包（Wallet）、钱包流水（WalletLog）、收藏（Favorite）、公告（Announcement）和价格历史（PriceHistory）。

### 4.2 实体关系详细描述

#### 4.2.1 用户与饰品库存（1:N）

一个用户（`debuff_user`）可以持有多个饰品实例（`debuff_inventory`），每个饰品实例归属于唯一的一个用户。关系通过 `debuff_inventory.user_id` 外键维护。

#### 4.2.2 饰品定义与饰品实例（1:N）

一种饰品定义（`debuff_skin`）可以对应多个饰品实例（`debuff_inventory`），因为同一款饰品可以被多个用户持有。每个实例通过 `debuff_inventory.skin_id` 关联到其饰品定义。

#### 4.2.3 饰品定义与武器类型（N:1）

多种饰品定义可以属于同一种武器类型（`debuff_weapon_type`）。通过 `debuff_skin.weapon_type_id` 外键关联。例如，AK-47 类型下可以有"红线"、"火蛇"等多种饰品。

#### 4.2.4 饰品定义与品质（N:1）

多种饰品定义可以拥有相同的品质等级（`debuff_skin_quality`）。通过 `debuff_skin.quality_id` 外键关联。品质等级包括崭新出厂、略有磨损、久经沙场、破损不堪、战痕累累五个级别。

#### 4.2.5 饰品实例与挂单（1:1）

每件饰品实例（`debuff_inventory`）最多只能对应一条在售挂单（`debuff_skin_listing`），通过 `debuff_skin_listing.inventory_id` 的唯一外键（UNIQUE）约束实现一对一关系。

#### 4.2.6 用户与挂单（1:N）

一个卖家用户（`debuff_user`）可以发布多条挂单（`debuff_skin_listing`），通过 `debuff_skin_listing.seller_id` 外键关联。

#### 4.2.7 挂单与订单（1:1）

一条挂单（`debuff_skin_listing`）成交后生成唯一一条订单（`debuff_order`），通过 `debuff_order.listing_id` 外键关联。挂单状态同步更新为"已售"。

#### 4.2.8 用户与订单（1:N）

一个用户可以作为买家或卖家参与多笔订单。订单表通过 `buyer_id` 和 `seller_id` 两个外键分别关联到买家和卖家用户。

#### 4.2.9 用户与求购单（1:N）

一个用户可以发布多条求购单（`debuff_buy_order`），通过 `debuff_buy_order.user_id` 外键关联。

#### 4.2.10 饰品定义与求购单（1:N）

一种饰品定义可以被多个用户求购，通过 `debuff_buy_order.skin_id` 外键关联。

#### 4.2.11 用户与钱包（1:1）

每个用户拥有唯一的钱包记录（`debuff_user_wallet`），通过 `debuff_user_wallet.user_id` 的唯一外键约束实现一对一关系。用户注册时自动初始化钱包。

#### 4.2.12 钱包与钱包流水（1:N）

一个钱包（`debuff_user_wallet`）可以产生多条流水记录（`debuff_wallet_log`），通过 `debuff_wallet_log.wallet_id` 外键关联。每笔充值、提现、交易均会生成对应的流水记录。

#### 4.2.13 用户与收藏（1:N）

一个用户可以收藏多个挂单，通过 `debuff_favorite.user_id` 外键关联。联合唯一索引 `(user_id, listing_id)` 确保同一用户对同一挂单不会重复收藏。

#### 4.2.14 饰品定义与价格历史（1:N）

一种饰品定义（`debuff_skin`）拥有多条价格历史记录（`debuff_price_history`），按日记录。联合唯一索引 `(skin_id, record_date)` 确保每种饰品每日只有一条价格记录。

### 4.3 关系矩阵

| 父实体 | 子实体 | 关系类型 | 关联字段 |
|--------|--------|---------|---------|
| debuff_user | debuff_inventory | 1:N | inventory.user_id → user.id |
| debuff_user | debuff_skin_listing | 1:N | listing.seller_id → user.id |
| debuff_user | debuff_order (buyer) | 1:N | order.buyer_id → user.id |
| debuff_user | debuff_order (seller) | 1:N | order.seller_id → user.id |
| debuff_user | debuff_buy_order | 1:N | buy_order.user_id → user.id |
| debuff_user | debuff_user_wallet | 1:1 | wallet.user_id → user.id (UNIQUE) |
| debuff_user | debuff_wallet_log | 1:N | wallet_log.user_id → user.id |
| debuff_user | debuff_favorite | 1:N | favorite.user_id → user.id |
| debuff_weapon_type | debuff_skin | 1:N | skin.weapon_type_id → weapon_type.id |
| debuff_skin_quality | debuff_skin | 1:N | skin.quality_id → quality.id |
| debuff_skin | debuff_inventory | 1:N | inventory.skin_id → skin.id |
| debuff_skin | debuff_skin_listing | 1:N | listing.skin_id → skin.id |
| debuff_skin | debuff_order | 1:N | order.skin_id → skin.id |
| debuff_skin | debuff_buy_order | 1:N | buy_order.skin_id → skin.id |
| debuff_skin | debuff_price_history | 1:N | price_history.skin_id → skin.id |
| debuff_inventory | debuff_skin_listing | 1:1 | listing.inventory_id → inventory.id (UNIQUE) |
| debuff_skin_listing | debuff_order | 1:1 | order.listing_id → listing.id |
| debuff_skin_listing | debuff_favorite | 1:N | favorite.listing_id → listing.id |
| debuff_user_wallet | debuff_wallet_log | 1:N | wallet_log.wallet_id → wallet.id |

---

## 5. 索引设计说明

### 5.1 索引设计原则

1. **主键索引**：所有表使用 `BIGINT AUTO_INCREMENT` 作为主键，InnoDB 聚簇索引自动优化。
2. **唯一索引**：业务唯一性约束通过唯一索引保障，如用户名、订单号、钱包用户映射、库存挂单映射等。
3. **外键索引**：所有外键字段均建立普通索引，加速 JOIN 查询和级联操作。
4. **查询优化索引**：针对高频查询条件（如状态字段、价格范围、时间范围）建立索引。
5. **联合索引**：针对多条件组合查询建立联合唯一索引，如 `(user_id, listing_id)` 防重复收藏、`(skin_id, record_date)` 防重复价格记录。

### 5.2 核心索引清单

| 表名 | 索引名 | 类型 | 字段 | 用途 |
|------|--------|------|------|------|
| debuff_user | uk_username | UNIQUE | username | 登录名唯一约束 |
| debuff_skin | idx_weapon_type | NORMAL | weapon_type_id | 按武器类型筛选饰品 |
| debuff_skin | idx_quality | NORMAL | quality_id | 按品质筛选饰品 |
| debuff_inventory | idx_user_id | NORMAL | user_id | 查询用户库存列表 |
| debuff_inventory | idx_skin_id | NORMAL | skin_id | 查询某饰品的所有实例 |
| debuff_skin_listing | uk_inventory | UNIQUE | inventory_id | 一件饰品只能挂一次 |
| debuff_skin_listing | idx_seller | NORMAL | seller_id | 查询卖家的所有挂单 |
| debuff_skin_listing | idx_skin | NORMAL | skin_id | 查询某饰品的所有在售挂单 |
| debuff_skin_listing | idx_price | NORMAL | price | 按价格排序/范围查询 |
| debuff_order | uk_order_no | UNIQUE | order_no | 订单号唯一约束 |
| debuff_order | idx_buyer | NORMAL | buyer_id | 查询买家的订单 |
| debuff_order | idx_seller | NORMAL | seller_id | 查询卖家的订单 |
| debuff_order | idx_create_time | NORMAL | create_time | 按时间排序/范围查询 |
| debuff_user_wallet | uk_user_id | UNIQUE | user_id | 一人一钱包约束 |
| debuff_wallet_log | idx_user_id | NORMAL | user_id | 查询用户流水 |
| debuff_wallet_log | idx_create_time | NORMAL | create_time | 按时间查询流水 |
| debuff_favorite | uk_user_listing | UNIQUE | user_id, listing_id | 防重复收藏 |
| debuff_price_history | uk_skin_date | UNIQUE | skin_id, record_date | 每种饰品每日一条记录 |

---

## 6. 关键业务规则与数据完整性约束

### 6.1 用户管理规则

| 规则编号 | 规则描述 |
|---------|---------|
| UR-01 | 用户名全局唯一，长度 1-30 字符，不可修改 |
| UR-02 | 密码使用 BCrypt 算法加密存储，不可逆 |
| UR-03 | 用户注册时自动创建钱包记录（余额为 0） |
| UR-04 | 用户状态为"停用"时，禁止登录及一切交易操作 |

### 6.2 饰品管理规则

| 规则编号 | 规则描述 |
|---------|---------|
| IR-01 | 饰品定义必须关联有效的武器类型和品质等级 |
| IR-02 | 饰品下架（status=1）后不可被新挂单引用 |
| IR-03 | 磨损度（float_value）取值范围 [0.000000, 1.000000]，精度 6 位小数 |
| IR-04 | 贴纸信息以 JSON 格式存储，包含贴纸名称、贴纸磨损等结构化数据 |

### 6.3 库存与挂单规则

| 规则编号 | 规则描述 |
|---------|---------|
| LR-01 | 一件饰品实例只能同时被一条挂单引用（inventory_id UNIQUE 约束） |
| LR-02 | 挂单上架时，库存状态同步更新为"在售"（status=1） |
| LR-03 | 挂单下架/取消时，库存状态恢复为"在库"（status=0） |
| LR-04 | 挂单售出时，库存所有权转移给买家，库存状态恢复为"在库" |
| LR-05 | 挂单可设置过期时间，过期后自动下架 |
| LR-06 | 挂单价格必须大于 0，精度为小数点后 2 位 |

### 6.4 交易订单规则

| 规则编号 | 规则描述 |
|---------|---------|
| OR-01 | 订单编号全局唯一，采用业务规则生成（如时间戳+序列号） |
| OR-02 | 订单创建时，挂单状态同步更新为"已售"（status=1） |
| OR-03 | 平台手续费 = 成交价格 * 手续费率（系统配置） |
| OR-04 | 卖家实际收入 = 成交价格 - 平台手续费 |
| OR-05 | 订单不可重复创建，一条挂单只能生成一条订单 |
| OR-06 | 订单取消时，挂单恢复为"在售"状态，资金回退 |

### 6.5 钱包与资金规则

| 规则编号 | 规则描述 |
|---------|---------|
| WR-01 | 每位用户有且仅有一个钱包（user_id UNIQUE 约束） |
| WR-02 | 可用余额（balance）不可为负数 |
| WR-03 | 挂单时冻结对应金额（frozen_amount 增加，balance 减少） |
| WR-04 | 取消挂单时解冻金额（frozen_amount 减少，balance 增加） |
| WR-05 | 成交时冻结金额转为卖家收入（frozen_amount 减少，卖家 balance 增加 seller_income） |
| WR-06 | 买入时扣减买家余额（balance 减少，buyer total_expense 增加） |
| WR-07 | 每笔资金变动必须记录流水（wallet_log），包含变动前后余额 |
| WR-08 | 流水的 balance_after 必须等于钱包当前 balance，保证数据一致性 |
| WR-09 | 钱包操作需在事务中完成，使用乐观锁或悲观锁防止并发问题 |

### 6.6 收藏规则

| 规则编号 | 规则描述 |
|---------|---------|
| FR-01 | 同一用户对同一挂单不可重复收藏（联合唯一约束） |
| FR-02 | 挂单下架/售出后，相关收藏记录可保留但不再展示 |

### 6.7 价格历史规则

| 规则编号 | 规则描述 |
|---------|---------|
| PR-01 | 每种饰品每日只记录一条价格数据（联合唯一约束） |
| PR-02 | 价格数据由定时任务每日凌晨汇总计算生成 |
| PR-03 | 当某日无成交记录时，可沿用前一交易日的价格或记录为 NULL |

### 6.8 数据一致性约束汇总

| 约束类型 | 说明 |
|---------|------|
| 实体完整性 | 所有表使用 BIGINT 自增主键，保证每行记录唯一可标识 |
| 参照完整性 | 所有外键关系在应用层维护，数据库层可选启用外键约束 |
| 域完整性 | status 字段使用 CHAR(1) 限定取值范围；金额字段使用 DECIMAL 避免浮点精度问题 |
| 用户自定义完整性 | 通过唯一索引、CHECK 约束（MySQL 8.0.16+）及事务保证业务规则 |
| 并发控制 | 钱包余额操作使用 `SELECT ... FOR UPDATE` 悲观锁或版本号乐观锁 |
| 事务管理 | 交易流程（创建订单 + 扣减余额 + 增加收入 + 记录流水 + 更新挂单/库存状态）在同一事务中完成 |

---

## 7. 附录

### 7.1 状态码速查表

| 表 | 字段 | 值 | 含义 |
|----|------|---|------|
| debuff_user | status | 0 | 正常 |
| debuff_user | status | 1 | 停用 |
| debuff_skin | status | 0 | 上架 |
| debuff_skin | status | 1 | 下架 |
| debuff_inventory | status | 0 | 在库 |
| debuff_inventory | status | 1 | 在售 |
| debuff_inventory | status | 2 | 冻结 |
| debuff_skin_listing | status | 0 | 在售 |
| debuff_skin_listing | status | 1 | 已售 |
| debuff_skin_listing | status | 2 | 已下架 |
| debuff_skin_listing | status | 3 | 已取消 |
| debuff_order | status | 0 | 待确认 |
| debuff_order | status | 1 | 已完成 |
| debuff_order | status | 2 | 已取消 |
| debuff_buy_order | status | 0 | 有效 |
| debuff_buy_order | status | 1 | 已完成 |
| debuff_buy_order | status | 2 | 已取消 |
| debuff_announcement | status | 0 | 正常显示 |
| debuff_announcement | status | 1 | 已关闭 |
| debuff_wallet_log | type | 1 | 充值 |
| debuff_wallet_log | type | 2 | 提现 |
| debuff_wallet_log | type | 3 | 买入 |
| debuff_wallet_log | type | 4 | 卖出 |
| debuff_wallet_log | type | 5 | 退款 |
| debuff_wallet_log | type | 6 | 手续费 |

### 7.2 武器类型枚举值

| 枚举值 | 中文名称 | 说明 |
|--------|---------|------|
| rifle | 步枪 | AK-47、M4A4、M4A1-S 等 |
| pistol | 手枪 | 沙漠之鹰、USP-S、格洛克 18 等 |
| smg | 冲锋枪 | MP9、MAC-10、UMP-45 等 |
| sniper | 狙击枪 | AWP、SSG 08 等 |
| shotgun | 霰弹枪 | XM1014、MAG-7 等 |
| knife | 刀具 | 蝴蝶刀、爪子刀、弯刀等 |
| glove | 手套 | 运动手套、专业手套等 |

### 7.3 磨损度与品质对应关系

| 品质名称 | 磨损度范围 | 颜色标识 |
|---------|-----------|---------|
| 崭新出厂 (Factory New) | 0.00 - 0.07 | 金色 |
| 略有磨损 (Minimal Wear) | 0.07 - 0.15 | 银色 |
| 久经沙场 (Field-Tested) | 0.15 - 0.38 | 铜色 |
| 破损不堪 (Well-Worn) | 0.38 - 0.45 | 灰色 |
| 战痕累累 (Battle-Scarred) | 0.45 - 1.00 | 暗灰色 |

---

> **文档维护说明：** 本文档随项目迭代持续更新。数据库结构变更时，需同步修改本文档对应章节，并在文档头部更新版本号。
