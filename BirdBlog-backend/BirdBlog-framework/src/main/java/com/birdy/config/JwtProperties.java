package com.birdy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 从配置文件读取 JWT 相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥(Base64 编码)
     */
    private String secretKey;

    /**
     * Token 有效期(毫秒)
     */
    private Long ttl;
}
