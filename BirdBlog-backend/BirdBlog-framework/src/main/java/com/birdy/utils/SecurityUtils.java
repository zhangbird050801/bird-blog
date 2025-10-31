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
            UserInfoVO userInfo = getUserInfo();
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
            // 1. 获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            HttpServletRequest request = attributes.getRequest();

            // 2. 从请求头获取token
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return null;
            }
            token = token.substring(7); // 移除 "Bearer " 前缀

            // 3. 解析token
            Claims claims = JwtUtil.parseJWT(token);
            if (claims == null) {
                return null;
            }

            // 4. 从claims中获取用户ID（JWT的subject存储的是用户ID字符串，如"15"）
            String userIdStr = claims.getSubject();
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return null;
            }

            // 5. 尝试解析为Long，如果失败说明可能是旧的JSON格式
            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                // 如果不是数字，尝试作为JSON解析（向后兼容）
                try {
                    return JSON.parseObject(userIdStr, UserInfoVO.class);
                } catch (Exception ex) {
                    return null;
                }
            }

            // 6. 先从Redis获取用户信息
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

            // 7. 如果Redis中没有，从数据库查询
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
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setNickName(user.getNickName());
        userInfoVO.setAvatar(user.getAvatar());
        userInfoVO.setSex(user.getSex() != null ? user.getSex().toString() : "2");
        userInfoVO.setEmail(user.getEmail());
        return userInfoVO;
    }
}

