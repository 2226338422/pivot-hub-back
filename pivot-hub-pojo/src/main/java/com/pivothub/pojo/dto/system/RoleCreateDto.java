package com.pivothub.pojo.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "新增角色")
public class RoleCreateDto {
    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_-]{1,64}$", message = "角色编码只能为1到64位字母、数字、下划线或短横线")
    @Schema(description = "角色编码")
    private String code;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称")
    private String name;

    @NotNull(message = "默认角色标记不能为空")
    @Min(value = 0, message = "默认角色标记只能为0或1")
    @Max(value = 1, message = "默认角色标记只能为0或1")
    @Schema(description = "是否默认角色 0-否 1-是")
    private Integer defa = 0;

    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序不能小于0")
    @Schema(description = "角色排序")
    private Integer sort = 0;

    @Size(max = 500, message = "备注不能超过500字")
    @Schema(description = "备注")
    private String remark;
}
