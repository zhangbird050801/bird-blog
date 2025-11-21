package com.birdy.utils;

import com.alibaba.fastjson.JSON;
import com.birdy.domain.entity.User;
import com.birdy.domain.vo.UserInfoVO;
import com.birdy.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Security 工具类
 * 用于获取当前登录用户信息
 *
 * @author Young
 * @date 2025/10/31
 */
@Component
public class SecurityUtils {

    private static RedisUtil redisUtil;
    private static UserService userService;

    /**
     * 注入 RedisUtil
     */
    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        SecurityUtils.redisUtil = redisUtil;
    }

    /**
     * 注入 UserService
     */
    @Autowired
    public void setUserService(UserService userService) {
        SecurityUtils.userService = userService;
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID，未登录返回null
     */
    public static Long getUserId() {
        try {
            // 方式1: 优先从 Spring Security Context 获取（由 JwtAuthenticationFilter 设置）
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() != null) {
                Object principal = authentication.getPrincipal();
                // JwtAuthenticationFilter 设置的是 String 类型的 userId
                if (principal instanceof String) {
                    return Long.parseLong((String) principal);
                }
            }
            
            // 方式2: 如果 SecurityContext 中没有，则从 Token 解析（兜底）
            UserInfoVO userInfo = getUserInfoFromToken();
            return userInfo != null ? userInfo.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息，未登录返回null
     */
    public static UserInfoVO getUserInfo() {
        try {
            // 1. 先获取用户 ID
            Long userId = getUserId();
            if (userId == null) {
                return null;
            }

            // 2. 先从 Redis 获取用户信息（缓存）
            String redisKey = "login:" + userId;
            Object userObj = redisUtil != null ? redisUtil.get(redisKey) : null;
            if (userObj != null) {
                String userJson;
                if (userObj instanceof String) {
                    userJson = (String) userObj;
                } else {
                    userJson = JSON.toJSONString(userObj);
                }
                User user = JSON.parseObject(userJson, User.class);
                return convertToUserInfoVO(user);
            }

            // 3. 如果 Redis 中没有，从数据库查询
            if (userService != null) {
                User user = userService.getById(userId);
                if (user != null) {
                    return convertToUserInfoVO(user);
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 Token 中解析用户信息（兜底方法）
     * 仅在 SecurityContext 中没有信息时使用
     * @return 用户信息，未登录返回null
     */
    private static UserInfoVO getUserInfoFromToken() {
        try {
            // 1. 获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            HttpServletRequest request = attributes.getRequest();

            // 2. 从请求头获取 token
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return null;
            }
            token = token.substring(7); // 移除 "Bearer " 前缀

            // 3. 解析 token
            Claims claims = JwtUtil.parseJWT(token);
            if (claims == null) {
                return null;
            }

            // 4. 从 claims 中获取用户 ID
            String userIdStr = claims.getSubject();
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return null;
            }

            // 5. 尝试解析为 Long
            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                // 如果不是数字，尝试作为 JSON 解析（向后兼容）
                try {
                    return JSON.parseObject(userIdStr, UserInfoVO.class);
                } catch (Exception ex) {
                    return null;
                }
            }

            // 6. 从 Redis 或数据库获取用户信息
            String redisKey = "login:" + userId;
            Object userObj = redisUtil != null ? redisUtil.get(redisKey) : null;
            if (userObj != null) {
                String userJson;
                if (userObj instanceof String) {
                    userJson = (String) userObj;
                } else {
                    userJson = JSON.toJSONString(userObj);
                }
                User user = JSON.parseObject(userJson, User.class);
                return convertToUserInfoVO(user);
            }

            if (userService != null) {
                User user = userService.getById(userId);
                if (user != null) {
                    return convertToUserInfoVO(user);
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将User实体转换为UserInfoVO
     */
    private static UserInfoVO convertToUserInfoVO(User user) {
        if (user == null) {
            return null;
        }

        try {
            // 使用UserService获取完整的用户信息（包括角色）
            return userService.getUserInfo(user.getId());
        } catch (Exception e) {
            // 如果获取完整信息失败，返回基本信息
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setId(user.getId());
            userInfoVO.setNickName(user.getNickName());
            userInfoVO.setAvatar(user.getAvatar());
            userInfoVO.setSex(user.getSex() != null ? user.getSex().toString() : "2");
            userInfoVO.setEmail(user.getEmail());
            userInfoVO.setType(user.getType());
            return userInfoVO;
        }
    }
}
