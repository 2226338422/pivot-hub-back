-- 在系统库执行。先执行 init-system-data.sql 至 init-system-data-4.sql。
-- 系统设置模块改造。

ALTER TABLE sys_permission MODIFY COLUMN menu_id VARCHAR(32) NULL COMMENT '菜单uuid';
ALTER TABLE sys_permission ADD COLUMN config_id VARCHAR(32) NULL COMMENT '系统配置uuid，关联 sys_config.uuid' AFTER menu_id;
ALTER TABLE sys_permission ADD KEY idx_sys_permission_config_id (config_id);

ALTER TABLE sys_config MODIFY COLUMN config_type TINYINT NULL COMMENT '表单类型 1-文本 2-开关 3-整数 4-小数 5-单选 6-多选';

-- ==================== sys_menu ====================
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), 'system_settings', '系统设置', parent.uuid,
       '/system/settings', 'el-icon-setting', 2, 1
FROM sys_menu parent
WHERE parent.uuid = (
    SELECT MIN(candidate.uuid)
    FROM sys_menu candidate
    WHERE candidate.code = 'system' AND candidate.parent_id = '0' AND candidate.status = 1
)
  AND NOT EXISTS (SELECT 1 FROM sys_menu existing WHERE existing.code = 'system_settings');

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
