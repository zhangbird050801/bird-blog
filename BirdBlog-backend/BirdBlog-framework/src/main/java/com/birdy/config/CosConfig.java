package com.birdy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云COS配置类
 *
 * @author Birdy
 * @date 2025/11/8
 */
@Data
@Component
@ConfigurationProperties(prefix = "cos")
public class CosConfig {

    /**
     * secretId
     */
    private String secretId;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 区域 例如：ap-beijing
     */
    private String region;

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 项目名称
     */
    private String projectName = "bird-blog";

    /**
     * 公共业务模块
     */
    private String common = "common";

    /**
     * 图片大小限制（M）
     */
    private String imageSize = "10";

    /**
     * 签名过期时间（分钟）
     */
    private int expiration = 30;
}