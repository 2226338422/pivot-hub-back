-- 在系统库执行。先执行 init-system-data.sql 至 init-system-data-5.sql。
-- 定时任务模块改造。

-- ==================== sys_menu ====================
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), 'scheduled_tasks', '定时任务', parent.uuid,
       '/system/scheduled-tasks', 'el-icon-time', 3, 1
FROM sys_menu parent
WHERE parent.uuid = (
    SELECT MIN(candidate.uuid)
    FROM sys_menu candidate
    WHERE candidate.code = 'system' AND candidate.parent_id = '0' AND candidate.status = 1
)
  AND NOT EXISTS (SELECT 1 FROM sys_menu existing WHERE existing.code = 'scheduled_tasks');

-- ==================== sys_permission ====================
-- 补齐管理员全部启用菜单权限，已有权限不重复添加。
INSERT INTO sys_permission (uuid, name, menu_id, config_id, role_id, user_id, person_type, status)
SELECT REPLACE(UUID(), '-', ''), menu.menu_name, menu.uuid, NULL, role.uuid, NULL, 1, 1
FROM sys_role role
INNER JOIN sys_menu menu
    ON menu.status = 1
WHERE role.code = 'admin' AND role.status = 1
  AND NOT EXISTS (
      SELECT 1
      FROM sys_permission existing
      WHERE existing.role_id = role.uuid AND existing.person_type = 1
        AND existing.menu_id = menu.uuid
  );
