package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体（关联角色或用户）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPermission extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 权限名称 */
    private String name;

    /** 菜单uuid */
    private String menuId;

    /** 角色uuid */
    private String roleId;

    /** 用户uuid */
    private String userId;

    /** 归属类型 1-角色 2-用户 */
    private Integer personType;

    /** 状态 0-禁用 1-正常 */
    private Integer status;
}
