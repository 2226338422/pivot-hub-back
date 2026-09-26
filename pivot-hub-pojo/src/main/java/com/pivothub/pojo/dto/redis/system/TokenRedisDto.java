package com.pivothub.pojo.dto.redis.system;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRedisDto {
    private String accessToken;
    private String refreshToken;
}
