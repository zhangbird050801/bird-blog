package com.birdy.config;

import com.birdy.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
            // 禁用 Session，使用无状态 JWT 认证
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // 配置授权规则
            .authorizeHttpRequests(authorize -> authorize
                // 允许 OPTIONS 请求（CORS 预检请求）
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
                    "/auth/logout",
                    "/auth/register",
                    "/admin/auth/login",
                    "/admin/auth/captcha",
                    "/admin/auth/logout"
                ).permitAll()
                // 博客前台公开接口放行
                .requestMatchers(
                    "/article/**",
                    "/category/**",
                    "/tag/**",
                    "/comment/**",
                    "/link/**"
                ).permitAll()
                // 管理后台认证接口放行（除了修改密码）
                .requestMatchers(
                    "/admin/auth/login",
                    "/admin/auth/captcha",
                    "/admin/auth/logout",
                    "/admin/auth/refresh-token"
                ).permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            // 添加 JWT 认证过滤器，在 UsernamePasswordAuthenticationFilter 之前执行
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
