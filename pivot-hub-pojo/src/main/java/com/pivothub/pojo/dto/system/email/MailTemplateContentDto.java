package com.pivothub.pojo.dto.system.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** 邮件模板的主题和正文。 */
@Data
@Schema(title = "邮件模板内容")
public class MailTemplateContentDto {
    /** 邮件主题。 */
    @Schema(description = "邮件主题")
    private String subject;
    /** 邮件正文。 */
    @Schema(description = "HTML 邮件正文")
    private String body;
}
