-- 在系统库执行。先执行 init-system-data.sql 和 init-system-data-2.sql。

-- ==================== sys_menu ====================
-- 在通知目录下添加站内信菜单。
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), 'inbox', '站内信', parent.uuid,
       '/notify/inbox', 'el-icon-message', 3, 1
FROM sys_menu parent
WHERE parent.uuid = (
    SELECT MIN(candidate.uuid)
    FROM sys_menu candidate
    WHERE candidate.code = 'notify'
      AND candidate.parent_id = '0'
      AND candidate.status = 1
)
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu existing WHERE existing.code = 'inbox'
  );

-- ==================== sys_permission ====================
-- 为管理员初始化站内信菜单权限。
INSERT INTO sys_permission (uuid, name, menu_id, role_id, user_id, person_type, status)
SELECT REPLACE(UUID(), '-', ''), menu.menu_name, menu.uuid, role.uuid, NULL, 1, 1
FROM sys_menu menu
INNER JOIN sys_role role ON role.code = 'admin' AND role.status = 1
WHERE menu.code = 'inbox'
  AND menu.status = 1
  AND NOT EXISTS (
      SELECT 1 FROM sys_permission existing
      WHERE existing.menu_id = menu.uuid
        AND existing.role_id = role.uuid
        AND existing.person_type = 1
  );
