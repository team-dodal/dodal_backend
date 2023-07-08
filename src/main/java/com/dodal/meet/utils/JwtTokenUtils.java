package com.dodal.meet.utils;

import com.dodal.meet.model.SocialType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtils {

//    private static final long EXPIRED_ACCESS_TIME = 5 * 60 * 1000L;
    private static final long EXPIRED_ACCESS_TIME = 60 * 1000L;
//    private static final long EXPIRED_REFRESH_TIME = 30 * 24 * 60 * 60 * 1000L;
    private static final long EXPIRED_REFRESH_TIME = 2 * 60 * 1000L;

    public static String getUserSocialId(String token, String key) {
        return extractClaims(token, key).get("socialId", String.class);
    }

    public static String getUserSocialType(String token, String key) {
        return extractClaims(token, key).get("socialType", String.class);
    }

    private static Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder().setSigningKey(getKey(key))
                .build().parseClaimsJws(token).getBody();
    }

    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date());
    }

    public static String generateAccessToken(String socialId, SocialType socialType, String key) {
        Claims claims = Jwts.claims();
        claims.put("socialId", socialId);
        claims.put("socialType", socialType.name());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_ACCESS_TIME ))
                .signWith(getKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(String socialId, SocialType socialType, String key) {
        Claims claims = Jwts.claims();
        claims.put("socialId", socialId);
        claims.put("socialType", socialType.name());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_REFRESH_TIME ))
                .signWith(getKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
