-- 在系统库执行。先执行 init-system-data.sql 至 init-system-data-3.sql。
-- 个人中心所在省份改造。
ALTER TABLE sys_user ADD COLUMN province_codes JSON NULL COMMENT '所在省份行政区划编码列表' AFTER avatar_file_id;
