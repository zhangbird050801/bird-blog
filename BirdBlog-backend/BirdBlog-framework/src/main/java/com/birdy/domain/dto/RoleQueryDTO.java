package com.birdy.domain.dto;

import lombok.Data;

/**
 * 角色查询DTO
 *
 * @author Birdy
 * @date 2025/11/21
 */
@Data
public class RoleQueryDTO {

    /**
     * 关键词（角色名称或编码）
     */
    private String keyword;

    /**
     * 状态：0正常，1禁用
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}