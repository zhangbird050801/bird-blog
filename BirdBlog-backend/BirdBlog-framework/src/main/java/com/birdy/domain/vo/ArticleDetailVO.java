package com.birdy.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 文章详情
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailVO {
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
     * 摘要
     */
    private String summary;

    /**
     * 正文内容
     */
    private String content;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 缩略图URL
     */
    private String thumbnail;

    /**
     * 浏览量
     */
    private Long viewCount;

    /**
     * 点赞量
     */
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 是否允许评论
     */
    private Boolean isComment;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishedTime;

    /**
     * 当前用户是否已点赞（仅当用户登录时返回）
     */
    private Boolean isLiked;
}
