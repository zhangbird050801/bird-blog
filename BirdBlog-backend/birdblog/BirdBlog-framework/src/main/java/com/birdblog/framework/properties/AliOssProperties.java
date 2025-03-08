package com.birdblog.framework.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Birdy
 * @date 2025/3/8 14:33
 * @description AliOssProperties
 */
@Component
@ConfigurationProperties(prefix = "birdblog.alioss")
@Data
public class AliOssProperties {
    private String endpoint;
    private String bucketName;
    private String region;
}