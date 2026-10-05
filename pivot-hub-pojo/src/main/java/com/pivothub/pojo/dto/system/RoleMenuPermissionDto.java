package com.pivothub.pojo.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "保存角色菜单权限")
public class RoleMenuPermissionDto {
    @NotBlank(message = "角色标识不能为空")
    @Size(max = 32, message = "角色标识过长")
    @Schema(description = "角色唯一标识")
    private String roleId;

    @NotNull(message = "菜单权限列表不能为空，清空时传空数组")
    @Schema(description = "勾选的菜单唯一标识")
    private List<@NotBlank(message = "菜单标识不能为空") @Size(max = 32, message = "菜单标识过长") String> menuIds;
}
