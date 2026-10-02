package com.pivothub.pojo.dto.system.feign;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "跨服务校验当前端访问令牌登录记录的请求")
public class VerifyAccessDto {
    @Schema(description = "用户唯一标识，由调用方验证访问令牌后解析得到")
    @NotBlank(message = "用户唯一标识不能为空")
    private String userId;

    @Schema(description = "客户端类型：1-Web，2-App")
    @NotNull(message = "客户端类型不能为空")
    @Min(value = 1, message = "客户端类型只能为1或2")
    @Max(value = 2, message = "客户端类型只能为1或2")
    private Integer clientType;

    @Schema(description = "调用方已验证签名、有效期及用途的访问令牌，不含Bearer前缀")
    @NotBlank(message = "访问令牌不能为空")
    private String accessToken;
}
