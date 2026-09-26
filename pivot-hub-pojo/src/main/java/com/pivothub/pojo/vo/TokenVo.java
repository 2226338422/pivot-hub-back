package com.pivothub.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: Token响应VO
 * @Author: lhb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录及续期令牌响应")
public class TokenVo {
    @Schema(description = "访问令牌")
    private String accessToken;
    @Schema(description = "刷新令牌")
    private String refreshToken;
    @Schema(description = "访问令牌有效期（秒）")
    private Long expiresIn;

    @Schema(description = "刷新令牌有效期（秒）")
    private Long refreshExpiresIn;

    @Schema(description = "客户端类型：1-Web，2-App")
    private Integer clientType;
}
