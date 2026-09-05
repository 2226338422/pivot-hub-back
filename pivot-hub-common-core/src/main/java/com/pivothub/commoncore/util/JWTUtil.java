package com.pivothub.commoncore.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JWTUtil {

    public static String generateToken(String subject, Long expireTime, String secret, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public static Claims parseToken(String token, String secret) {
        String tokenValue = token.substring(7);
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(tokenValue)
                .getBody();
    }
}
