package com.pivothub.pojo.dto.system;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailLoginDto {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须为6位数字")
    private String code;

    @Schema(description = "客户端类型：1-Web，2-App")
    @NotNull(message = "客户端类型不能为空")
    @Min(value = 1, message = "客户端类型只能为1或2")
    @Max(value = 2, message = "客户端类型只能为1或2")
    private Integer clientType;
}
