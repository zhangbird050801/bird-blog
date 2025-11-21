package com.birdy.domain.dto;

import lombok.Data;

/**
 * 角色状态更新DTO
 *
 * @author Birdy
 * @date 2025/11/21
 */
@Data
public class RoleStatusUpdateDTO {

    /**
     * 角色状态 (0:正常 1:停用)
     */
    private Integer status;
}