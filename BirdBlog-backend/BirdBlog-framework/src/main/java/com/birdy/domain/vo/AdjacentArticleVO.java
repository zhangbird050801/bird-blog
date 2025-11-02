package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Birdy
 * @date 2025/11/2 22:05
 * @description AdjacentArticleVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjacentArticleVO {
    /**
     * 文章ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * URL别名
     */
    private String slug;

    /**
     * 缩略图URL
     */
    private String thumbnail;
}