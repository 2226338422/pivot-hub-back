package com.pivothub.pojo.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "修改角色")
public class RoleUpdateDto {
    @NotBlank(message = "角色标识不能为空")
    @Size(max = 32, message = "角色标识过长")
    @Schema(description = "角色唯一标识")
    private String roleId;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称")
    private String name;

    @Min(value = 0, message = "默认角色标记只能为0或1")
    @Max(value = 1, message = "默认角色标记只能为0或1")
    @Schema(description = "是否默认角色 0-否 1-是，不传则保留原值")
    private Integer defa;

    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序不能小于0")
    @Schema(description = "角色排序")
    private Integer sort;

    @Size(max = 500, message = "备注不能超过500字")
    @Schema(description = "备注")
    private String remark;
}
