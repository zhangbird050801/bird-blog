package com.birdy.domain.dto;

import lombok.Data;

/**
 * 登录请求 DTO
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 用户登录请求参数
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码 UUID(用于从 Redis 获取验证码)
     */
    private String uuid;
}
