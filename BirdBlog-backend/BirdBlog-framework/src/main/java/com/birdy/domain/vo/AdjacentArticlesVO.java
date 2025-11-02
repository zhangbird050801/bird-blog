package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Birdy
 * @date 2025/11/2 22:08
 * @description AdjacentArticlesVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjacentArticlesVO {
    /**
     * 上一篇文章
     */
    private AdjacentArticleVO previous;

    /**
     * 下一篇文章
     */
    private AdjacentArticleVO next;
}