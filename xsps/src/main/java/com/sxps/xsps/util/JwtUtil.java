package com.sxps.xsps.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "xsps-merchant-secret";
    private static final long ACCESS_EXPIRE = 1000 * 60 * 30;   // 30分钟
    private static final long REFRESH_EXPIRE = 1000L * 60 * 60 * 24 * 7; // 7天

    /** 生成 access token */
    public static String genAccess(Long uid, String username) {
        return JWT.create()
                .withClaim("uid", uid)
                .withClaim("username", username)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXPIRE))
                .sign(Algorithm.HMAC256(SECRET));
    }

    /** 生成 refresh token */
    public static String genRefresh(Long uid, String username) {
        return JWT.create()
                .withClaim("uid", uid)
                .withClaim("username", username)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXPIRE))
                .sign(Algorithm.HMAC256(SECRET));
    }

    /** 校验 token */
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }

    /** 解析 refresh token，不抛异常 */
    public static Map<String, Object> parse(String token) {
        try {
            DecodedJWT jwt = verify(token);
            Map<String, Object> map = new HashMap<>();
            map.put("uid", jwt.getClaim("uid").asLong());
            map.put("username", jwt.getClaim("username").asString());
            return map;
        } catch (Exception e) {
            return null;
        }
    }
}
