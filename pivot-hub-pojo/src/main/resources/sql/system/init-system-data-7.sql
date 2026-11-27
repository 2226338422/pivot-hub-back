-- 在系统库执行。先执行 init-system-data.sql 至 init-system-data-6.sql。
-- 台风预测定时任务和消息模板改造。

-- ==================== sys_scheduled_task ====================
INSERT INTO sys_scheduled_task
    (uuid, task_code, task_name, service_name, cron, status, sort, remark)
SELECT REPLACE(UUID(), '-', ''), 'typhoon_forecast', '台风预报提醒', 'business',
       '0 0 10 * * *', 1, 1, '根据台风预测路径和用户所在省份发送提醒'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_scheduled_task WHERE task_code = 'typhoon_forecast'
);

-- ==================== sys_message_template ====================
UPDATE sys_message_template
SET body_template = CONCAT('本次台风提醒共 {arg_1} 条。', CHAR(10), CHAR(10), '{arg_2}'),
    item_template = '- 台风「{arg_1}」｜{arg_2}'
WHERE message_type = 'typhoon_forecast';
