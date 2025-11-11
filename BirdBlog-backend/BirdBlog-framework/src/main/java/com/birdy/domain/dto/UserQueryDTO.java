package com.birdy.domain.dto;

import lombok.Data;

/**
 * 用户查询条件 DTO
 */
@Data
public class UserQueryDTO {

    /**
     * 页码，默认 1
     */
    private Integer pageNum = 1;

    /**
     * 每页数量，默认 10
     */
    private Integer pageSize = 10;

    /**
     * 关键词（支持用户名、昵称、邮箱、手机号模糊查询）
     */
    private String keyword;

    /**
     * 用户状态：0正常，1停用
     */
    private Integer status;

    /**
     * 用户类型：0普通，1管理员
     */
    private Integer type;
}
