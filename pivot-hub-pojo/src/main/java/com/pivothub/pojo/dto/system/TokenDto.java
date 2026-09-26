package com.pivothub.pojo.dto.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Token 相关请求，按接口使用不同校验分组。 */
@Data
public class TokenDto {
    public interface Refresh {
    }

    @NotBlank(message = "刷新令牌不能为空", groups = Refresh.class)
    private String refreshToken;
}
