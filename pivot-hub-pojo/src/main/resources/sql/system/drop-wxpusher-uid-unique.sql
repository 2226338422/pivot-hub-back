-- PivotHub 系统库：移除 sys_user_platform 的 wxpusher_uid 唯一键。
-- 背景：解绑只把 wxpusher_status 置 0、保留 wxpusher_uid；同一微信号换绑到不同账号时，
--       会在不同用户行留下相同的 uid，唯一键会阻塞绑定。
-- 生效性改由业务保证：同一 uid 至多一行 wxpusher_status = 1。
-- 注意：MySQL 的 DROP INDEX 不支持 IF EXISTS，索引不存在时执行会报错，请先确认索引存在。

ALTER TABLE sys_user_platform DROP INDEX uk_sys_user_platform_wxpusher_uid;
