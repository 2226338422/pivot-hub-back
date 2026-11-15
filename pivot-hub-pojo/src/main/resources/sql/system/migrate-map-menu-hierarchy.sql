-- 将已有地图页面拆为“地图”目录和“地点收藏”子菜单，在系统库执行。
-- 已有环境先执行本脚本，再按需运行 init-system-data.sql；新环境直接运行初始化脚本。
-- 前置条件：map、map_favorites 菜单编码各自唯一，旧版地图菜单为顶级 /map 页面。
-- 保留原菜单 UUID、角色和直接用户授权及禁用状态；顺序执行时可重复运行。

START TRANSACTION;

-- 1. 原地图菜单改为地点收藏，保留 UUID 和原有授权关联。
UPDATE sys_menu original
LEFT JOIN sys_menu existing ON existing.code = 'map_favorites'
SET original.code = 'map_favorites', original.menu_name = '地点收藏',
    original.web_url = '/map/favorites', original.icon = 'el-icon-star-off'
WHERE original.code = 'map' AND original.parent_id = '0'
  AND original.web_url = '/map' AND existing.uuid IS NULL;

-- 2. 新增地图父目录，继承原顶级排序和状态，再将地点收藏挂到父目录下。
INSERT INTO sys_menu (uuid, code, menu_name, parent_id, web_url, icon, sort, status)
SELECT REPLACE(UUID(), '-', ''), 'map', '地图', '0', NULL,
       'el-icon-map-location', child.sort, child.status
FROM sys_menu child
WHERE child.code = 'map_favorites' AND child.parent_id = '0'
  AND NOT EXISTS (SELECT 1 FROM sys_menu existing WHERE existing.code = 'map');

UPDATE sys_menu child
INNER JOIN sys_menu parent ON parent.code = 'map' AND parent.parent_id = '0' AND parent.web_url IS NULL
SET child.parent_id = parent.uuid, child.sort = 0
WHERE child.code = 'map_favorites' AND child.parent_id = '0';

-- 3. 更新原默认权限名称，保留自定义名称及授权状态。
UPDATE sys_permission permission
INNER JOIN sys_menu menu ON menu.uuid = permission.menu_id AND menu.code = 'map_favorites'
SET permission.name = '地点收藏访问'
WHERE permission.name = '地图访问';

-- 4. 为原角色和直接用户授权补齐父目录；已存在的父目录授权不覆盖。
INSERT INTO sys_permission (uuid, name, menu_id, role_id, user_id, person_type, status)
SELECT REPLACE(UUID(), '-', ''), '地图访问', grants.menu_id,
       grants.role_id, grants.user_id, grants.person_type, grants.status
FROM (
    SELECT parent.uuid AS menu_id, permission.role_id, permission.user_id,
           permission.person_type, MAX(permission.status) AS status
    FROM sys_permission permission
    INNER JOIN sys_menu child ON child.uuid = permission.menu_id AND child.code = 'map_favorites'
    INNER JOIN sys_menu parent ON parent.uuid = child.parent_id AND parent.code = 'map'
    GROUP BY parent.uuid, permission.role_id, permission.user_id, permission.person_type
) grants
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission existing
    WHERE existing.menu_id = grants.menu_id AND existing.person_type = grants.person_type
      AND existing.role_id <=> grants.role_id AND existing.user_id <=> grants.user_id
);

COMMIT;
