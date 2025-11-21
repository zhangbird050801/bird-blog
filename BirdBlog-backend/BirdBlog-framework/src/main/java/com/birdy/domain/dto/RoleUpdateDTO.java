package com.birdy.domain.dto;

import lombok.Data;

/**
 * 更新角色DTO
 *
 * @author Birdy
 * @date 2025/11/21
 */
@Data
public class RoleUpdateDTO {

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：0正常，1禁用
     */
    private Integer status;
}