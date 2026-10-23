-- =============================================
-- Debuff 预置数据脚本
-- 执行顺序：在 debuff_business_init.sql 之后执行
-- =============================================

-- ----------------------------
-- 品质等级（7级）
-- ----------------------------
INSERT INTO debuff_skin_quality (id, quality_name, quality_color, sort_order) VALUES
(1, '消费级',   '#B0C3D9', 1),
(2, '工业级',   '#5E98D9', 2),
(3, '军规级',   '#4B69FF', 3),
(4, '受限',     '#8847FF', 4),
(5, '保密',     '#D32CE6', 5),
(6, '隐秘',     '#EB4B4B', 6),
(7, '违禁',     '#E4AE39', 7);

-- ----------------------------
-- 武器类型（按分类）
-- ----------------------------
-- 步枪
INSERT INTO debuff_weapon_type (weapon_name, weapon_category, sort_order) VALUES
('AK-47',      'rifle', 1),
('M4A4',       'rifle', 2),
('M4A1-S',     'rifle', 3),
('FAMAS',      'rifle', 4),
('Galil AR',   'rifle', 5),
('SG 553',     'rifle', 6),
('AUG',        'rifle', 7);

-- 手枪
INSERT INTO debuff_weapon_type (weapon_name, weapon_category, sort_order) VALUES
('Desert Eagle',  'pistol', 10),
('USP-S',         'pistol', 11),
('Glock-18',      'pistol', 12),
('P250',          'pistol', 13),
('Five-SeveN',    'pistol', 14),
('Tec-9',         'pistol', 15),
('CZ75 Auto',     'pistol', 16),
('Dual Berettas', 'pistol', 17),
('P2000',         'pistol', 18),
('R8 Revolver',   'pistol', 19);

-- 冲锋枪
INSERT INTO debuff_weapon_type (weapon_name, weapon_category, sort_order) VALUES
('MP9',     'smg', 20),
('MAC-10',  'smg', 21),
('UMP-45',  'smg', 22),
('P90',     'smg', 23),
('PP-Bizon','smg', 24),
('MP7',     'smg', 25);

-- 狙击枪
INSERT INTO debuff_weapon_type (weapon_name, weapon_category, sort_order) VALUES
('AWP',     'sniper', 30),
('SSG 08',  'sniper', 31),
('SCAR-20', 'sniper', 32),
('G3SG1',   'sniper', 33);

-- 霰弹枪
INSERT INTO debuff_weapon_type (weapon_name, weapon_category, sort_order) VALUES
('Nova',       'shotgun', 40),
('XM1014',     'shotgun', 41),
('MAG-7',      'shotgun', 42),
('Sawed-Off',  'shotgun', 43);

-- 刀
INSERT INTO debuff_weapon_type (weapon_name, weapon_category, sort_order) VALUES
('蝴蝶刀',     'knife', 50),
('爪子刀',     'knife', 51),
('M9刺刀',     'knife', 52),
('弯刀',       'knife', 53),
('猎杀者匕首', 'knife', 54),
('暗影双匕',   'knife', 55),
('刺刀',       'knife', 56),
('折叠刀',     'knife', 57),
('穿肠刀',     'knife', 58),
('锯齿爪刀',   'knife', 59);

-- 手套
INSERT INTO debuff_weapon_type (weapon_name, weapon_category, sort_order) VALUES
('运动手套',   'glove', 60),
('驾驶手套',   'glove', 61),
('专业手套',   'glove', 62),
('摩托手套',   'glove', 63),
('迷彩手套',   'glove', 64);

-- ----------------------------
-- 示例饰品（10款热门皮肤）
-- ----------------------------
INSERT INTO debuff_skin (skin_name, weapon_type_id, quality_id, image_url, description, min_price) VALUES
('火蛇',       1,  6, '', 'AK-47 | 火蛇 (Field-Tested)，经典热门皮肤', 299.00),
('二龙',       18, 5, '', 'AWP | 二龙 (Field-Tested)，龙主题涂装', 189.00),
('皇帝',       2,  6, '', 'M4A4 | 皇帝 (Minimal Wear)，金色龙纹', 259.00),
('印花集',     8,  6, '', 'Desert Eagle | 印花集 (Factory New)', 89.00),
('霓虹骑士',   1,  6, '', 'AK-47 | 霓虹骑士 (Factory New)', 459.00),
('印花集',     3,  5, '', 'M4A1-S | 印花集 (Field-Tested)', 169.00),
('杀意确认',   9,  6, '', 'USP-S | 杀意确认 (Minimal Wear)', 129.00),
('渐变之色',   38, 6, '', '爪子刀 | 渐变之色 (Factory New)', 2999.00),
('虎牙',       37, 6, '', '蝴蝶刀 | 虎牙 (Factory New)', 3499.00),
('潘多拉魔盒', 61, 6, '', '运动手套 | 潘多拉魔盒 (Minimal Wear)', 1999.00);

-- ----------------------------
-- 测试用户钱包（为sys_user中已有的测试用户创建钱包）
-- 假设admin用户ID=1，测试用户ID=2
-- ----------------------------
INSERT INTO debuff_user_wallet (user_id, balance, frozen_amount, total_recharge) VALUES
(1, 10000.00, 0.00, 10000.00),
(2, 5000.00,  0.00, 5000.00);

-- ----------------------------
-- 测试库存数据（给测试用户一些饰品）
-- ----------------------------
INSERT INTO debuff_inventory (user_id, skin_id, float_value, wear_name, source, status) VALUES
(1, 1,  0.250000, '久经沙场', 'upload', 0),
(1, 2,  0.120000, '略有磨损', 'upload', 0),
(1, 5,  0.050000, '崭新出厂', 'upload', 0),
(2, 3,  0.350000, '久经沙场', 'upload', 0),
(2, 4,  0.030000, '崭新出厂', 'upload', 0),
(2, 7,  0.100000, '略有磨损', 'upload', 0),
(2, 10, 0.080000, '略有磨损', 'upload', 0);

-- ----------------------------
-- 菜单数据（饰品交易相关菜单）
-- ----------------------------
-- 一级菜单：饰品市场
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('饰品市场', 0, 1, 'market', NULL, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '饰品市场目录');

-- 获取刚插入的菜单ID（用变量方便后续引用）
SET @marketMenuId = LAST_INSERT_ID();

-- 二级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('市场浏览', @marketMenuId, 1, 'list', 'market/index', 'C', '0', '0', 'trade:market:list', 'buy', 'admin', sysdate(), '饰品市场浏览'),
('我的库存', @marketMenuId, 2, 'inventory', 'inventory/index', 'C', '0', '0', 'trade:inventory:list', 'list', 'admin', sysdate(), '我的库存'),
('我的交易', @marketMenuId, 3, 'trade', 'trade/orders', 'C', '0', '0', 'trade:order:list', 'swap', 'admin', sysdate(), '我的交易'),
('我的钱包', @marketMenuId, 4, 'wallet', 'wallet/index', 'C', '0', '0', 'trade:wallet:info', 'money', 'admin', sysdate(), '我的钱包'),
('我的收藏', @marketMenuId, 5, 'favorites', 'user/favorites', 'C', '0', '0', 'trade:favorite:list', 'star', 'admin', sysdate(), '我的收藏');

-- 一级菜单：交易管理（后台管理）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('交易管理', 0, 2, 'admin/trade', NULL, 'M', '0', '0', '', 'peoples', 'admin', sysdate(), '交易后台管理目录');

SET @adminTradeMenuId = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('饰品管理', @adminTradeMenuId, 1, 'skin',   'admin/skin/index',       'C', '0', '0', 'admin:skin:list',       'component', 'admin', sysdate(), '饰品审核管理'),
('订单管理', @adminTradeMenuId, 2, 'order',  'admin/order/index',      'C', '0', '0', 'admin:order:list',      'log',       'admin', sysdate(), '交易订单管理'),
('钱包管理', @adminTradeMenuId, 3, 'wallet', 'admin/wallet/index',     'C', '0', '0', 'admin:wallet:list',     'dict',      'admin', sysdate(), '用户钱包管理'),
('公告管理', @adminTradeMenuId, 4, 'notice', 'admin/announcement/index','C', '0', '0', 'admin:notice:list',     'message',   'admin', sysdate(), '平台公告管理'),
('数据看板', @adminTradeMenuId, 5, 'dashboard','admin/dashboard',      'C', '0', '0', 'admin:dashboard:view',  'chart',     'admin', sysdate(), '数据统计看板');
