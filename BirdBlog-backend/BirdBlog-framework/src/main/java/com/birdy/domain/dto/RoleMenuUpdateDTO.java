package com.birdy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 角色菜单权限更新DTO
 *
 * @author Birdy
 * @date 2025/11/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuUpdateDTO {

    /**
     * 菜单ID列表
     */
    private List<Long> menuIds;
}