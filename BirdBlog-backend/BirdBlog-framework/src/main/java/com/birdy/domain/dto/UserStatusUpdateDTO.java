package com.birdy.domain.dto;

import lombok.Data;

/**
 * 用户状态更新 DTO
 */
@Data
public class UserStatusUpdateDTO {
    /**
     * 用户状态：0正常，1停用
     */
    private Integer status;
}
