package com.birdblog.framework.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Birdy
 * @date 2025/3/8 13:38
 * @description Knife4jConfig
 */
@Configuration
public class Knife4jConfig {
    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            // 可以自定义一些配置，如：
            // 配置全局鉴权参数-Authorize
            // 根据@Tag 上的排序，写入x-order
        };
    }

    @Bean
    public OpenAPI openApi() {

        return new OpenAPI().info(new io.swagger.v3.oas.models.info.Info()
                .description("BirdBlog")
                .version("v1.0.0")
                .title("BirdBlog 接口文档"));
    }
}
