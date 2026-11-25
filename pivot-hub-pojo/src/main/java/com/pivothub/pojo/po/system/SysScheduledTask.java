package com.pivothub.pojo.po.system;

import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统定时任务。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysScheduledTask extends BasePo {
    private String uuid;

    /** 任务编码。 */
    private String taskCode;

    /** 任务名称。 */
    private String taskName;

    /** 执行任务的服务名称。 */
    private String serviceName;

    /** Spring Cron 表达式。 */
    private String cron;

    /** 状态 0-停用 1-启用。 */
    private Integer status;

    /** 排序。 */
    private Integer sort;

    /** 备注。 */
    private String remark;
}
