package com.birdy.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户角色更新DTO
 *
 * @author Birdy
 * @date 2025/11/21
 */
@Data
public class UserRoleUpdateDTO {

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;
}