package com.birdy.filter;

import com.birdy.utils.JwtUtil;
import com.birdy.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT 认证过滤器
 * 用于从请求头中提取 JWT Token 并进行验证
 *
 * @author Birdy
 * @date 2025/10/31
 * @description 每个请求都会经过此过滤器，验证 JWT Token 的有效性和黑名单状态
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 1. 从请求头获取 Token
        String token = extractToken(request);
        
        // 2. 如果 Token 存在且有效，设置认证信息
        if (token != null && !token.isEmpty()) {
            try {
                // 2.1 检查 Token 是否在黑名单中（已退出登录）
                String blacklistKey = "blacklist:" + token;
                if (redisUtil.hasKey(blacklistKey)) {
                    logger.warn("Token 已被加入黑名单(用户已退出登录)");
                    filterChain.doFilter(request, response);
                    return;
                }

                // 2.2 解析 Token
                Claims claims = JwtUtil.parseJWT(token);
                
                if (claims != null) {
                    // 从 claims 中获取用户 ID
                    String userId = claims.getSubject();
                    
                    // 创建认证对象（无需密码，因为已通过 JWT 验证）
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userId,           // principal: 用户ID
                            null,             // credentials: 无需密码
                            new ArrayList<>() // authorities: 权限列表（暂为空）
                        );
                    
                    // 将认证信息设置到 Security Context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Token 解析失败，不设置认证信息
                // 后续会被 Spring Security 拦截返回 403
                logger.warn("JWT Token 解析失败: " + e.getMessage());
            }
        }
        
        // 3. 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头提取 Token
     *
     * @param request HTTP 请求
     * @return Token 字符串，如果不存在则返回 null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 移除 "Bearer " 前缀
        }
        
        return null;
    }
}
