package com.birdy.utils;

import com.birdy.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 用于生成和解析 JWT Token
 */
@Component
public class JwtUtil {

    private static JwtProperties jwtProperties;

    /**
     * 注入 JWT 配置属性
     */
    @Autowired
    public void setJwtProperties(JwtProperties jwtProperties) {
        JwtUtil.jwtProperties = jwtProperties;
    }

    /**
     * 获取 UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成 JWT
     *
     * @param subject token 中要存放的数据(JSON 格式)
     * @return JWT 字符串
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    /**
     * 生成 JWT(自定义过期时间)
     *
     * @param subject   token 中要存放的数据(JSON 格式)
     * @param ttlMillis token 超时时间(毫秒)
     * @return JWT 字符串
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());
        return builder.compact();
    }

    /**
     * 创建 JWT Builder
     */
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = jwtProperties.getTtl();
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              // 唯一 ID
                .setSubject(subject)      // 主题(可以是 JSON 数据)
                .setIssuer("birdblog")    // 签发者
                .setIssuedAt(now)         // 签发时间
                .signWith(signatureAlgorithm, secretKey) // 使用 HS256 对称加密算法签名
                .setExpiration(expDate);  // 设置过期时间
    }

    /**
     * 生成 JWT(自定义 ID)
     *
     * @param id        唯一标识
     * @param subject   主题
     * @param ttlMillis 超时时间
     * @return JWT 字符串
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);
        return builder.compact();
    }

    /**
     * 生成 RefreshToken (有效期7天)
     *
     * @param subject token 中要存放的数据
     * @return RefreshToken 字符串
     */
    public static String createRefreshToken(String subject) {
        // RefreshToken 有效期设置为 7 天
        long refreshTokenTtl = 7 * 24 * 60 * 60 * 1000L; // 7天(毫秒)
        return createJWT(subject, refreshTokenTtl);
    }

    /**
     * 解析 JWT
     *
     * @param jwt JWT 字符串
     * @return Claims 对象
     * @throws Exception 解析异常
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 检查 Token 是否在黑名单中
     *
     * @param token JWT token
     * @return true=已失效, false=有效
     */
    public static boolean isTokenBlacklisted(String token) {
        // 这里需要注入 RedisUtil,暂时返回 false
        // 实际使用时需要从 Redis 查询 blacklist:{token}
        return false;
    }

    /**
     * 生成加密后的密钥 SecretKey
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
    }
}
