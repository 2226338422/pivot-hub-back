package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysConfig extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 配置键，唯一 */
    private String configKey;

    /** 配置值（敏感值 AES 加密） */
    private String configValue;

    /** 配置名称 */
    private String configName;

    /** 类型 1-文本 2-JSON 3-加密 */
    private Integer configType;

    /** 状态 0-禁用 1-正常 */
    private Integer status;

    private String remark;
}
