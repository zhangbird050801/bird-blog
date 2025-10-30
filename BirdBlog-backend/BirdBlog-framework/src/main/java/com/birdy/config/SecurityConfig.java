package com.birdy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * 
 * @author Birdy
 * @date 2025/10/30
 * @description 配置安全策略,放行 Swagger、登录等静态资源
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码加密器
     * 
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤链
     * 
     * @param http HttpSecurity 对象
     * @return SecurityFilterChain 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF 保护(开发环境可禁用,生产环境需根据实际情况启用)
            .csrf(AbstractHttpConfigurer::disable)
            // 配置授权规则
            .authorizeHttpRequests(authorize -> authorize
                // Swagger UI 相关端点放行
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // 登录认证相关端点放行
                .requestMatchers(
                    "/auth/login",
                    "/auth/captcha",
                    "/auth/logout"
                ).permitAll()
                // 博客前台公开接口放行
                .requestMatchers(
                    "/article/**",
                    "/category/**",
                    "/tag/**",
                    "/comment/**"
                ).permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
