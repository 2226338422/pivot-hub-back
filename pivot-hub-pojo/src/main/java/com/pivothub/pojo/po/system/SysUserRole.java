package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-角色关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserRole extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    private String userId;

    private String roleId;
}
