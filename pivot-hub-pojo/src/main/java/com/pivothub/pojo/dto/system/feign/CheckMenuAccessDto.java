package com.pivothub.pojo.dto.system.feign;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "内部服务查询指定用户的菜单访问资格")
public class CheckMenuAccessDto {
    @Schema(description = "用户标识，由调用服务从已认证上下文取得")
    @NotBlank(message = "用户标识不能为空")
    @Size(max = 32, message = "用户标识最多32字")
    private String userId;

    @Schema(description = "菜单编码")
    @NotBlank(message = "菜单编码不能为空")
    @Size(max = 64, message = "菜单编码最多64字")
    private String menuCode;
}
