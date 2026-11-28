package com.pivothub.pojo.po.system;

import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 邮件模板。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMailTemplate extends BasePo {
    /** 主键。 */
    private String uuid;
    /** 模板编码。 */
    private String templateCode;
    /** 模板名称。 */
    private String templateName;
    /** 邮件主题模板。 */
    private String subjectTemplate;
    /** HTML 邮件正文模板。 */
    private String bodyTemplate;
    /** 状态：0-停用，1-启用。 */
    private Integer status;
}
