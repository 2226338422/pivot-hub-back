package com.pivothub.pojo.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-角色关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserRole extends BasePo {
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色ID")
    private Long roleId;
}
