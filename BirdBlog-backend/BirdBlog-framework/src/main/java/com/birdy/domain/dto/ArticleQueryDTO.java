package com.birdy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章查询条件 DTO
 *
 * @author Birdy
 * @date 2025/11/8
 * @description 文章列表查询请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleQueryDTO {

    /**
     * 当前页码，默认第1页
     */
    private Integer pageNum = 1;

    /**
     * 每页数量，默认10条
     */
    private Integer pageSize = 10;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 文章状态：0-已发布，1-草稿
     */
    private Integer status;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 是否置顶
     */
    private Boolean isTop;
}