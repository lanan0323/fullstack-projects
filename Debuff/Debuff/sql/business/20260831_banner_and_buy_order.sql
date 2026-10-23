-- 首页轮播图与求购成交功能增量脚本
-- 请在现有 Debuff 业务库执行；本脚本不删除或重建任何现有数据。

CREATE TABLE IF NOT EXISTS debuff_banner (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  title       VARCHAR(128) NOT NULL COMMENT '主标题',
  subtitle    VARCHAR(256) DEFAULT '' COMMENT '副标题',
  image_url   VARCHAR(512) NOT NULL COMMENT '轮播背景图片',
  link_url    VARCHAR(512) DEFAULT '' COMMENT '点击跳转地址',
  sort_order  INT          DEFAULT 0 COMMENT '排序号(越小越靠前)',
  status      CHAR(1)      DEFAULT '0' COMMENT '状态(0发布 1停用)',
  create_by   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  remark      VARCHAR(256) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户端首页轮播图';

-- 确保后台「交易管理」目录存在；使用菜单名称判断，可重复执行。
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '交易管理', 0, 2, 'admin/trade', NULL, 'M', '0', '0', '', 'peoples', 'admin', sysdate(), '交易后台管理目录'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '交易管理' AND parent_id = 0);

SET @trade_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '交易管理' AND parent_id = 0 LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '轮播管理', @trade_menu_id, 6, 'banner', 'trade/banner/index', 'C', '0', '0', 'trade:banner:list', 'picture', 'admin', sysdate(), '用户端首页轮播图配置'
WHERE @trade_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'trade:banner:list');

SET @banner_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'trade:banner:list' LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '轮播查询', @banner_menu_id, 1, '#', '', 'F', '0', '0', 'trade:banner:query', '#', 'admin', sysdate(), ''
WHERE @banner_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'trade:banner:query');
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '轮播新增', @banner_menu_id, 2, '#', '', 'F', '0', '0', 'trade:banner:add', '#', 'admin', sysdate(), ''
WHERE @banner_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'trade:banner:add');
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '轮播修改', @banner_menu_id, 3, '#', '', 'F', '0', '0', 'trade:banner:edit', '#', 'admin', sysdate(), ''
WHERE @banner_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'trade:banner:edit');
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '轮播删除', @banner_menu_id, 4, '#', '', 'F', '0', '0', 'trade:banner:remove', '#', 'admin', sysdate(), ''
WHERE @banner_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'trade:banner:remove');

-- 为管理员角色授予交易目录、轮播菜单及操作权限。
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE (m.menu_id = @trade_menu_id OR m.perms IN ('trade:banner:list', 'trade:banner:query', 'trade:banner:add', 'trade:banner:edit', 'trade:banner:remove'))
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id);
