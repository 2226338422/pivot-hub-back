package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    private String code;

    private String name;

    /** 是否默认角色 0-否 1-是 */
    private Integer defa;

    /** 排序 */
    private Integer sort;

    /** 状态 0-禁用 1-正常 */
    private Integer status;

    private String remark;
}
