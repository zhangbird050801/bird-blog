package com.birdy.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * 更新用户信息请求类
 */
@Data
public class UpdateUserInfoRequest {

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 性别：0男，1女，2未知
     */
    private Integer sex;

    /**
     * 头像URL
     */
    private String avatar;
}