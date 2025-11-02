package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Birdy
 * @date 2025/11/2 21:24
 * @description RelatedArticleVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatedArticleVO {
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
     * 分类名称
     */
    private String categoryName;

    /**
     * 缩略图URL
     */
    private String thumbnail;
}
