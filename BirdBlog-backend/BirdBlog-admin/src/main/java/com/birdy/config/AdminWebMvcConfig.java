package com.birdy.config;

import com.birdy.interceptor.AdminAccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 管理后台 WebMvc 配置
 */
@Configuration
public class AdminWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AdminAccessInterceptor adminAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAccessInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/auth/**", "/error");
    }
}
