-- 在系统库执行。先执行 system.sql，再按顺序执行 init-system-data.sql 至 init-system-data-8.sql。
-- 初始化邮箱验证码 HTML 模板，保留已有模板内容。

INSERT INTO sys_mail_template
    (uuid, template_code, template_name, subject_template, body_template, status)
SELECT REPLACE(UUID(), '-', ''),
       'email_code',
       '邮箱验证码',
       '【枢集】您的安全验证码',
       CONCAT(
           '<html><body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,sans-serif;">',
           '<div style="width:100%;padding:40px 0;">',
           '<div style="max-width:600px;margin:0 auto;background-color:#ffffff;border-radius:8px;overflow:hidden;">',
           '<div style="background:linear-gradient(to bottom,#ffffff,#b2ebf2);padding:20px;text-align:center;">',
           '<img src="cid:Logo" alt="Logo" style="height:80px;width:auto;display:inline-block;"/>',
           '<div style="color:#006064;font-size:18px;margin-top:10px;font-weight:bold;">枢集</div></div>',
           '<div style="padding:40px 30px;">',
           '<h2 style="margin:0 0 20px;color:#333;font-size:24px;">身份验证</h2>',
           '<p style="color:#666;font-size:16px;line-height:1.5;">您好，{name}：</p>',
           '<p style="color:#666;font-size:16px;line-height:1.5;">您正在进行{scene}操作，本次验证码如下：</p>',
           '<div style="background-color:#f8f9fa;border-left:4px solid #4a90e2;padding:15px;margin:25px 0;text-align:center;">',
           '<span style="font-size:32px;font-weight:bold;color:#4a90e2;letter-spacing:5px;">{code}</span></div>',
           '<p style="color:#999;font-size:14px;">请勿将此验证码泄露给他人。</p>',
           '<p style="color:#999;font-size:14px;">该验证码将在 <strong>{ttl}分钟</strong> 后失效。</p>',
           '</div><div style="background-color:#f4f6f8;padding:20px;text-align:center;color:#999;font-size:12px;">',
           '<p style="margin:0;">系统邮件，请勿回复</p></div></div></div></body></html>'
       ),
       1
WHERE NOT EXISTS (
    SELECT 1 FROM sys_mail_template WHERE template_code = 'email_code'
);
