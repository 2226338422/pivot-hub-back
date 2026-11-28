-- 在系统库执行。先执行 init-system-data.sql 至 init-system-data-7.sql。
-- 新增台风预报查询时间系统设置。

ALTER TABLE sys_config MODIFY COLUMN config_type TINYINT NULL
    COMMENT '表单类型 1-文本 2-开关 3-整数 4-小数 5-单选 6-多选 7-日期时间';

-- ==================== sys_config ====================
INSERT INTO sys_config (uuid, config_key, config_value, config_name, config_type, status, remark)
SELECT REPLACE(UUID(), '-', ''), 'typhoon.forecast.query_time', '',
       '台风预报查询时间', 7, 1, '按北京时间填写，留空使用任务实际执行时间'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE config_key = 'typhoon.forecast.query_time'
);

-- ==================== sys_permission ====================
INSERT INTO sys_permission (uuid, name, menu_id, config_id, role_id, user_id, person_type, status)
SELECT REPLACE(UUID(), '-', ''), config.config_name, NULL, config.uuid, role.uuid, NULL, 1, 1
FROM sys_role role
INNER JOIN sys_config config
    ON config.config_key = 'typhoon.forecast.query_time' AND config.status = 1
WHERE role.code = 'admin' AND role.status = 1
  AND NOT EXISTS (
      SELECT 1
      FROM sys_permission existing
      WHERE existing.role_id = role.uuid AND existing.person_type = 1
        AND existing.config_id = config.uuid
  );
