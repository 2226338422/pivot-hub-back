package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 用户-角色关联实体
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserRole extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 用户uuid */
    private String userId;

    /** 角色uuid */
    private String roleId;
}
