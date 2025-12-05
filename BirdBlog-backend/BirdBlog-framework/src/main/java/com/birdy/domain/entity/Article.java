package com.birdy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章表
 * @TableName bg_article
 */
@TableName(value ="bg_article")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class Article {
    /**
     * 文章ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * URL别名（便于SEO）
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
     * 作者(用户ID)
     */
    private Long authorId;

    /**
     * 缩略图URL
     */
    private String thumbnail;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 状态：0已发布，1草稿
     */
    private Integer status;

    /**
     * 允许评论
     */
    private Boolean isComment;

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
     * 发布时间
     */
    private Date publishedTime;

    /**
     * 置顶时间
     */
    private Date pinnedTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Boolean deleted;
}