package com.pivothub.system.util;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.smtp.SMTPMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Value("${spring.mail.sender-name}")
    private String senderName;

    @Value("${logoUrl:}")
    private String logoUrl;

    /** 批量密送同一封纯文本邮件，返回 SMTP 接受的邮箱。  暂时不做图片处理*/
    public Set<String> sendBccEmail(String subject, String content, List<String> emails) {
        SMTPMessage message = new SMTPMessage(mailSender.createMimeMessage().getSession());
        message.setSendPartial(true);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(senderAddress());
            helper.setBcc(emails.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(content);
        } catch (MessagingException failure) {
            throw new MailPreparationException(failure);
        }
        try {
            mailSender.send(message);
            return Set.copyOf(emails);
        } catch (MailSendException failure) {
            Exception[] messageExceptions = failure.getMessageExceptions();
            Throwable cause = messageExceptions.length == 0 ? failure.getCause() : messageExceptions[0];
            if (cause instanceof SendFailedException sendFailure && sendFailure.getValidSentAddresses() != null) {
                return Arrays.stream(sendFailure.getValidSentAddresses())
                        .map(InternetAddress.class::cast)
                        .map(InternetAddress::getAddress)
                        .collect(Collectors.toSet());
            }
            throw failure;
        }
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
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        helper.setFrom(senderAddress());
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
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        helper.setFrom(senderAddress());
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
        helper.setFrom(senderAddress());
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
     * @param subject 邮件主题
     * @param htmlContent 已渲染的 HTML 正文
     * @param toEmail 目标邮箱
     * @return boolean
     * @Author lhb
     * @CreateTime 2025/12/22 21:57
     */
    public boolean sendEmailCode(String subject, String htmlContent, String toEmail) {
        String logoId = "Logo";
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

    /** 构造带显示名称的发件人地址。 */
    private InternetAddress senderAddress() {
        try {
            return new InternetAddress(from, senderName, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException failure) {
            throw new MailPreparationException(failure);
        }
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
