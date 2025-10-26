package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Birdy
 * @date 2025/10/26 15:04
 * @description HotArticleVO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotArticleVO {
    /**
     * 文章ID
     */
    private Long id;

    /**
     * URL别名（便于SEO）
     */
    private String slug;

    /**
     * 标题
     */
    private String title;

    /**
     * 浏览量
     */
    private Long viewCount;

    /**
     * 缩略图URL
     */
    private String thumbnail;
}
