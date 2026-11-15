-- 为已有环境补充“地图 -> 台风预测”静态页面菜单，在系统库执行。
-- 前置条件：已存在启用的 map 顶级目录，web_url 为 NULL；旧版 /map 环境先完成地图菜单迁移。
-- 为普通用户和管理员角色补齐菜单授权；其他角色可通过角色管理页面单独授权。
-- 仅补缺失数据，顺序执行时可重复运行；已有菜单及禁用授权不覆盖。

START TRANSACTION;

-- 1. 台风预测作为地图目录的第二个子菜单。
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), 'map_typhoon', '台风预测', parent.uuid,
       '/map/typhoon', 'el-icon-wind-power', 1, 1
FROM sys_menu parent
WHERE parent.code = 'map' AND parent.parent_id = '0' AND parent.web_url IS NULL AND parent.status = 1
  AND parent.uuid = (
      SELECT MIN(candidate.uuid) FROM sys_menu candidate
      WHERE candidate.code = 'map' AND candidate.parent_id = '0'
        AND candidate.web_url IS NULL AND candidate.status = 1
  )
  AND NOT EXISTS (SELECT 1 FROM sys_menu existing WHERE existing.code = 'map_typhoon');

-- 2. 普通用户和管理员角色补齐父目录及台风预测页面授权。
INSERT INTO sys_permission (uuid, name, menu_id, role_id, user_id, person_type, status)
SELECT REPLACE(UUID(), '-', ''), CONCAT(menu.menu_name, '访问'),
       menu.uuid, role.uuid, NULL, 1, 1
FROM sys_menu menu
INNER JOIN sys_role role ON role.code IN ('default_user', 'admin') AND role.status = 1
WHERE menu.status = 1 AND menu.code IN ('map', 'map_typhoon')
  AND NOT EXISTS (
      SELECT 1 FROM sys_permission existing
      WHERE existing.menu_id = menu.uuid AND existing.role_id = role.uuid AND existing.person_type = 1
  );

COMMIT;
