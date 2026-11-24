package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 系统配置实体
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysConfig extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 配置键，唯一 */
    private String configKey;

    /** 配置值，按文本保存 */
    private String configValue;

    /** 配置名称 */
    private String configName;

    /** 表单类型 1-文本 2-开关 3-整数 4-小数 5-单选 6-多选 */
    private Integer configType;

    /** 状态 0-禁用 1-正常 */
    private Integer status;

    /** 备注 */
    private String remark;
}
