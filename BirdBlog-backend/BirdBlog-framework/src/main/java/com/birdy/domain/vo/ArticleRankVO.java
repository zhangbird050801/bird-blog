package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 文章排行视图对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRankVO {

    private Long articleId;

    private String title;

    private String slug;

    private Long viewCount;

    private Long likeCount;

    private Long favoriteCount;

    private Long commentCount;

    /**
     * 互动总分（点赞+收藏）
     */
    private Long engagementScore;

    private Date publishedTime;
}
