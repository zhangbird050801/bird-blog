package com.birdy.domain.dto;

import lombok.Data;

/**
 * 修改密码请求 DTO
 *
 * @author Birdy
 * @date 2025/11/25
 * @description 修改密码请求参数
 */
@Data
public class ChangePasswordRequestDTO {

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认新密码
     */
    private String confirmPassword;
}