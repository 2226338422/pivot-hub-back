package com.pivothub.common.util;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Objects;

/**
 * @ClassName: EmailApiUtil
 * @Description:
 * @Author: lhb
 * @Date: 2026/1/28
 */

@Component
@Slf4j
public class EmailApiUtil {
    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from ;// 发件人

    @Value("${logoUrl:}")
    private String logoUrl;

    /**
     * @Description: 发送纯文本的邮件
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @return boolean
     * @Author lhb
     * @CreateTime 2025/12/22 22:05
     */
    @SneakyThrows(Exception.class)
    public boolean sendGeneralEmail(String subject, String content, String... to){
        // 创建邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        // 设置收件人
        message.setTo(to);
        // 设置邮件主题
        message.setSubject(subject);
        // 设置邮件内容
        message.setText(content);
        // 发送邮件
        mailSender.send(message);
        return true;
    }

    /**
     * @Description: 发送html的邮件，非图片
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @return boolean
     * @Author lhb
     * @CreateTime 2025/12/22 22:09
     */
    @SneakyThrows(Exception.class)
    public boolean sendHtmlEmail(String subject, String content, String... to){
        // 创建邮件消息
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        // 设置收件人
        helper.setTo(to);
        // 设置邮件主题
        helper.setSubject(subject);
        // 设置邮件内容
        helper.setText(content, true);
        // 发送邮件
        mailSender.send(mimeMessage);
        return true;
    }

    /**
     * @Description: 发送带附件的邮件, 待改造，附件路径是本地路径
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @param filePaths  附件路径
     * @return boolean
     * @Author lhb
     * @CreateTime 2025/12/22 22:09
     */
    @SneakyThrows(Exception.class)
    public boolean sendAttachmentsEmail(String subject, String content, String[] to, String[] filePaths) {
        // 创建邮件消息
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        // 设置收件人
        helper.setTo(to);
        // 设置邮件主题
        helper.setSubject(subject);
        // 设置邮件内容
        helper.setText(content,true);
        // 添加附件
        if (filePaths != null) {
            for (String filePath : filePaths) {
                FileSystemResource file = new FileSystemResource(new File(filePath));
                helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

            }
        }
        // 发送邮件
        mailSender.send(mimeMessage);
        return true;
    }

    /**
     * @Description: 发送带静态资源（内嵌网络图片）的邮件
     * @param subject 邮件主题
     * @param content 邮件正文（HTML格式，图片标签需写成 <img src="cid:rscId">）
     * @param rscUrl 图片的网络URL地址
     * @param rscId 资源ID，需要与 content 中的 cid:xxx 对应
     * @param to 收件人数组
     * @return boolean
     * @Author lhb
     * @CreateTime 2025/12/22 22:04
     */
    @SneakyThrows(Exception.class)
    public boolean sendInlineResourceEmail(String subject, String content, String rscUrl, String rscId, String... to) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // true 表示 multipart，用于同时发送 HTML 和内嵌资源
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        // true 表示 HTML 内容
        helper.setText(content, true);
        UrlResource res;
        try {
            res = new UrlResource(rscUrl);
            if (!res.exists() && !res.isReadable()) {
                log.warn("网络图片资源可能无法读取: {}", rscUrl);
                return false;
            }
        } catch (MalformedURLException e) {
            log.error("图片URL格式错误: {}", rscUrl);
            return false;
        }
        // 将下载下来的图片资源绑定到 CID 上,也就是如果图片cid属性要和这个一致
        helper.addInline(rscId, res);
        mailSender.send(mimeMessage);
        return true;
    }

    /**
     * @Description: 邮箱发送验证码方法
     * @param code 验证码
     * @param ttl 有效期?几分钟，对应redisConstants的对应的ttl
     * @param toEmail 目标邮箱
     * @return boolean
     * @Author lhb
     * @CreateTime 2025/12/22 21:57
     */
    public boolean sendEmailCode(String code, Long ttl, String toEmail) {
        String subject = "【电商商城】您的安全验证码";
        String logoId = "Logo";
        // 构建 HTML 内容
        String htmlContent = "<html>" +
                "<body style=\"margin: 0; padding: 0; background-color: #f4f6f8; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;\">" +
                "  <div style=\"width: 100%; padding: 40px 0;\">" +
                "    <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); overflow: hidden;\">" +
                "      " +
                "      <div style=\"background-color: #b2ebf2; background: linear-gradient(to bottom, #ffffff, #b2ebf2); padding: 20px; text-align: center;\">" +
                "        <img src=\"cid:" + logoId + "\" alt=\"Logo\" style=\"height: 80px; width: auto; mix-blend-mode: multiply; display: inline-block;\" />" +
                "        <div style=\"color: #006064; font-size: 18px; margin-top: 10px; font-weight: bold;\">个人兴趣开发-微服务电商平台</div>" +
                "      </div>" +
                "      " +
                "      " +
                "      <div style=\"padding: 40px 30px;\">" +
                "        <h2 style=\"margin: 0 0 20px; color: #333; font-size: 24px;\">身份验证</h2>" +
                "        <p style=\"color: #666; font-size: 16px; line-height: 1.5;\">亲爱的用户，您好：</p>" +
                "        <p style=\"color: #666; font-size: 16px; line-height: 1.5;\">您正在进行登录或绑定操作，本次请求的验证码如下：</p>" +
                "        " +
                "        <div style=\"background-color: #f8f9fa; border-left: 4px solid #4a90e2; padding: 15px; margin: 25px 0; text-align: center;\">" +
                "          <span style=\"font-size: 32px; font-weight: bold; color: #4a90e2; letter-spacing: 5px;\">" + code + "</span>" +
                "        </div>" +
                "        " +
                "        <p style=\"color: #999; font-size: 14px;\">⚠️为了您的账号安全，请勿将此验证码泄露给他人</p>" +
                "        <p style=\"color: #999; font-size: 14px;\">该验证码将在 <strong>" +
                ttl.toString() +
                "分钟</strong> 后失效。</p>" +
                "      </div>" +
                "      " +
                "      <div style=\"background-color: #f4f6f8; padding: 20px; text-align: center; color: #999; font-size: 12px;\">" +
                "        <p style=\"margin: 0;\">网站域名：luhongbin.site</p>" +
                "        <p style=\"margin: 5px 0 0;\">系统邮件，请勿回复</p>" +
                "      </div>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
        if (!StringUtils.hasText(logoUrl)) {
            htmlContent = htmlContent.replaceAll("<img[^>]*>", "");
            return sendHtmlEmail(subject, htmlContent, toEmail);
        }
        //调用发送带静态资源（内嵌网络图片）的邮件方法
        return sendInlineResourceEmail(
                subject,
                htmlContent,
                logoUrl,
                logoId,
                toEmail
        );
    }

    /**
     * @Description: 邮箱号脱敏处理
     * @param email
     * @return String
     * @Author lhb
     * @CreateTime 2026/1/4 0:00
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return email;
        }
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex); // 包含'@'符号
        String maskedUsername;
        if (username.length() >= 3) {
            int starCount = username.length() - 2;
            maskedUsername = username.charAt(0) + "*".repeat(starCount) + username.charAt(username.length() - 1);
        } else {
            maskedUsername = username.charAt(0) + "*";
        }
        return maskedUsername + domain;
    }

}
