package com.pivothub.commoncore.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JWTUtil {

    public static String generateToken(String subject, Long expireTime, String secret, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims == null ? Map.of() : claims)
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public static Claims parseToken(String token, String secret) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("令牌不能为空");
        }
        String tokenValue = token.trim();
        if (tokenValue.regionMatches(true, 0, "Bearer ", 0, 7)) {
            tokenValue = tokenValue.substring(7).trim();
        }
        if (tokenValue.isBlank()) {
            throw new IllegalArgumentException("令牌不能为空");
        }
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(tokenValue)
                .getBody();
    }
}
