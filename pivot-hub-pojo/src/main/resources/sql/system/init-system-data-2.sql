-- 在系统库执行；先运行 system.sql 建表，再执行 init-system-data.sql。
-- 只补缺失数据，不覆盖已有菜单、通知渠道、消息订阅默认配置、角色或权限。

-- ==================== sys_menu ====================
-- 通知目录下的消息订阅管理和消息模板管理菜单。
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), seed.code, seed.menu_name, parent.uuid,
       seed.web_url, seed.icon, seed.sort, 1
FROM (
    SELECT 'message_subscription' AS code, '消息订阅管理' AS menu_name,
           '/notify/subscriptions' AS web_url, 'el-icon-message' AS icon, 1 AS sort
    UNION ALL
    SELECT 'message_template', '消息模板管理',
           '/notify/templates', 'el-icon-document', 2
) seed
CROSS JOIN sys_menu parent
WHERE parent.uuid = (
    SELECT MIN(candidate.uuid)
    FROM sys_menu candidate
    WHERE candidate.code = 'notify'
      AND candidate.parent_id = '0'
      AND candidate.status = 1
)
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu existing WHERE existing.code = seed.code
  );

-- ==================== sys_message_subscription ====================
-- 默认订阅按来源菜单编码关联，公告推送默认开启，台风预测默认关闭。
INSERT INTO sys_message_subscription
    (uuid, user_id, message_type, menu_id, name, detail, enabled, is_default, sort)
SELECT REPLACE(UUID(), '-', ''), NULL, seed.message_type, menu.uuid, seed.name, seed.detail,
       seed.enabled, 1, seed.sort
FROM (
    SELECT 'public_push' AS message_type, 'notice_list' AS menu_code,
           '公告推送' AS name, '接收新公告发布通知' AS detail, 1 AS enabled, 1 AS sort
    UNION ALL
    SELECT 'typhoon_forecast', 'map_typhoon', '台风预测', '接收台风预报更新通知', 0, 2
) seed
INNER JOIN sys_menu menu ON menu.uuid = (
    SELECT MIN(candidate.uuid)
    FROM sys_menu candidate
    WHERE candidate.code = seed.menu_code
      AND candidate.status = 1
)
WHERE NOT EXISTS (
    SELECT 1 FROM sys_message_subscription existing
    WHERE existing.message_type = seed.message_type
      AND existing.user_id IS NULL
      AND existing.is_default = 1
);

-- ==================== sys_message_template ====================
-- 初始化公告推送和台风预测模板，保留已有模板的内容。
INSERT INTO sys_message_template (uuid, message_type, body_template, item_template)
SELECT REPLACE(UUID(), '-', ''), seed.message_type, seed.body_template, seed.item_template
FROM (
    SELECT 'public_push' AS message_type,
           '{arg_1}' AS body_template,
           NULL AS item_template
    UNION ALL
    SELECT 'typhoon_forecast',
           CONCAT('预计有 {arg_1} 个台风可能影响本地区，请注意防范。', CHAR(10), CHAR(10), '{arg_2}'),
           '- 台风「{arg_1}」预计于 {arg_2} 影响本地区。'
) seed
WHERE NOT EXISTS (
    SELECT 1 FROM sys_message_template existing
    WHERE existing.message_type = seed.message_type
);

-- ==================== sys_permission ====================
-- 管理员获得消息订阅管理和消息模板管理的菜单权限。
INSERT INTO sys_permission (uuid, name, menu_id, role_id, user_id, person_type, status)
SELECT REPLACE(UUID(), '-', ''), menu.menu_name,
       menu.uuid, role.uuid, NULL, 1, 1
FROM sys_menu menu
INNER JOIN sys_role role ON role.code = 'admin' AND role.status = 1
WHERE menu.status = 1
  AND menu.code IN ('message_subscription', 'message_template')
  AND NOT EXISTS (
      SELECT 1 FROM sys_permission existing
      WHERE existing.menu_id = menu.uuid
        AND existing.role_id = role.uuid
        AND existing.person_type = 1
  );
