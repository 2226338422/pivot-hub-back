package com.pivothub.commoncore.util;

import org.apache.commons.lang3.StringUtils;

public final class AuthHeaderUtil {
    private AuthHeaderUtil() {
    }

    public static String extractBearerToken(String authorization) {
        if (StringUtils.isBlank(authorization)) {
            return null;
        }
        String value = authorization.trim();
        if (!value.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }
        String token = value.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}
