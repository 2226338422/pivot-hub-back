package com.pivothub.pojo.po.system;

import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 用户通知渠道设置实体（含全局默认行）
 * @Author lhb
 * @CreateTime 2026/11/8 15:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNotifySetting extends BasePo {
    private String uuid;

    /** 用户uuid，NULL 表示全局默认行 */
    private String userId;

    /** 通知渠道 inbox/email/wxpusher */
    private String channel;

    /** 渠道名称 */
    private String name;

    /** 渠道说明文案 */
    private String detail;

    /** 是否开启 0-关闭 1-开启 */
    private Integer enabled;

    /** 是否需绑定 0-无需绑定 1-需绑定 */
    private Integer requireBind;

    /** 0-用户设置 1-全局默认 */
    private Integer isDefault;

    /** 排序，正序展示 */
    private Integer sort;
}
