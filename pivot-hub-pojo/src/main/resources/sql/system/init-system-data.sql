-- 在系统库执行；先运行 system.sql 建表。
-- 只补缺失数据，不覆盖已有菜单、通知渠道、角色或权限。

-- v1.0

-- 管理员角色。
INSERT INTO sys_role (uuid, code, name, defa, sort, status)
SELECT REPLACE(UUID(), '-', ''), 'admin', '管理员', 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'admin');

-- 顶级菜单。
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), seed.code, seed.menu_name, '0',
       seed.web_url, seed.icon, seed.sort, 1
FROM (
    SELECT 'home' AS code, '首页' AS menu_name, '/home' AS web_url,
           'el-icon-s-home' AS icon, 0 AS sort
    UNION ALL
    SELECT 'community', '社区', NULL, 'el-icon-chat-dot-round', 10
    UNION ALL
    SELECT 'system', '系统', NULL, 'el-icon-setting', 20
    UNION ALL
    SELECT 'map', '地图', NULL, 'el-icon-map-location', 30
    UNION ALL
    SELECT 'notify', '通知', NULL, 'el-icon-bell', 40
) seed
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu existing WHERE existing.code = seed.code
);

-- 功能菜单。
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), seed.code, seed.menu_name, parent.uuid,
       seed.web_url, seed.icon, seed.sort, 1
FROM (
    SELECT 'community_developer' AS code, '开发者社区' AS menu_name,
           'community' AS parent_code, '/community/developer' AS web_url,
           'el-icon-cpu' AS icon, 0 AS sort
    UNION ALL
    SELECT 'community_public', '公共社区', 'community', '/community/public',
           'el-icon-chat-line-round', 1
    UNION ALL
    SELECT 'role_management', '角色管理', 'system', '/system/roles',
           'el-icon-user-solid', 0
    UNION ALL
    SELECT 'map_favorites', '地点收藏', 'map', '/map/favorites',
           'el-icon-star-off', 0
    UNION ALL
    SELECT 'map_typhoon', '台风预测', 'map', '/map/typhoon',
           'el-icon-wind-power', 1
    UNION ALL
    SELECT 'notify_setting', '通知设置', 'notify', '/notify/settings',
           'el-icon-setting', 0
) seed
INNER JOIN sys_menu parent
        ON parent.code = seed.parent_code
       AND parent.parent_id = '0'
       AND parent.status = 1
       AND parent.uuid = (
           SELECT MIN(candidate.uuid)
           FROM sys_menu candidate
           WHERE candidate.code = seed.parent_code
             AND candidate.parent_id = '0'
             AND candidate.status = 1
       )
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu existing WHERE existing.code = seed.code
);

-- 通知渠道默认行。
INSERT INTO sys_notify_setting
    (uuid, user_id, channel, name, detail, enabled, require_bind, is_default, sort)
SELECT REPLACE(UUID(), '-', ''), NULL, seed.channel, seed.name, seed.detail,
       seed.enabled, seed.require_bind, 1, seed.sort
FROM (
    SELECT 'inbox' AS channel, '站内信' AS name, '消息将发送到站内信' AS detail,
           1 AS enabled, 0 AS require_bind, 1 AS sort
    UNION ALL
    SELECT 'email', '邮箱', '绑定邮箱后可通过邮件接收通知',
           0, 0, 2
    UNION ALL
    SELECT 'wxpusher', 'WxPusher 消息推送平台',
           '使用微信绑定后，可通过WxPusher 消息推送平台接收推送',
           0, 1, 3
) seed
WHERE NOT EXISTS (
    SELECT 1 FROM sys_notify_setting existing
    WHERE existing.channel = seed.channel AND existing.is_default = 1
);

-- 管理员拥有当前所有启用菜单的权限。
INSERT INTO sys_permission (uuid, name, menu_id, role_id, user_id, person_type, status)
SELECT REPLACE(UUID(), '-', ''), menu.menu_name,
       menu.uuid, role.uuid, NULL, 1, 1
FROM sys_menu menu
INNER JOIN sys_role role ON role.code = 'admin' AND role.status = 1
WHERE menu.status = 1
  AND NOT EXISTS (
      SELECT 1 FROM sys_permission existing
      WHERE existing.menu_id = menu.uuid
        AND existing.role_id = role.uuid
        AND existing.person_type = 1
  );
-- v1.1

-- 一级公告目录。
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), 'notice', '公告', '0',
       NULL, 'el-icon-document', 50, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_menu existing WHERE existing.code = 'notice');

-- 公告目录下的功能菜单。
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), seed.code, seed.menu_name, parent.uuid,
       seed.web_url, seed.icon, seed.sort, 1
FROM (
    SELECT 'notice_list' AS code, '公告列表' AS menu_name,
           '/notice/list' AS web_url, 'el-icon-document' AS icon, 0 AS sort
    UNION ALL
    SELECT 'notice_manage', '公告管理', '/notice/manage',
           'el-icon-edit-outline', 1
) seed
INNER JOIN sys_menu parent ON parent.code = 'notice'
                          AND parent.parent_id = '0'
                          AND parent.status = 1
WHERE parent.uuid = (
    SELECT MIN(candidate.uuid)
    FROM sys_menu candidate
    WHERE candidate.code = 'notice'
      AND candidate.parent_id = '0'
      AND candidate.status = 1
)
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu existing WHERE existing.code = seed.code
  );

-- 管理员获得公告目录及子菜单权限。
INSERT INTO sys_permission (uuid, name, menu_id, role_id, user_id, person_type, status)
SELECT REPLACE(UUID(), '-', ''), menu.menu_name,
       menu.uuid, role.uuid, NULL, 1, 1
FROM sys_menu menu
INNER JOIN sys_role role ON role.code = 'admin' AND role.status = 1
WHERE menu.status = 1
  AND menu.code IN ('notice', 'notice_list', 'notice_manage')
  AND NOT EXISTS (
      SELECT 1 FROM sys_permission existing
      WHERE existing.menu_id = menu.uuid
        AND existing.role_id = role.uuid
        AND existing.person_type = 1
  );
