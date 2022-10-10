package cn.manpok.blogsystem.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Calendar;
import java.util.Map;

/**
 * JWT工具类
 */
public class JWTUtil {

    /**
     * 签名的密钥
     */
    private static final String SECRET = "929223489b49a198a77c5865510c1dde";

    /**
     * 生成token
     *
     * @param payload token携带的信息
     * @return token字符串
     */
    public static String generateToken(Map<String, String> payload) {
        // 指定token过期时间
        Calendar calendar = Calendar.getInstance();
        // 2小时
        calendar.add(Calendar.HOUR, 2);
        JWTCreator.Builder builder = JWT.create();
        // 构建payload
        payload.forEach(builder::withClaim);
        // 指定过期时间和签名算法，并返回token
        String token = builder.withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(SECRET));
        return token;
    }

    /**
     * 解析token
     *
     * @param token token字符串
     * @return 解析后的token类
     */
    public static DecodedJWT decodeToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT;
    }
}
