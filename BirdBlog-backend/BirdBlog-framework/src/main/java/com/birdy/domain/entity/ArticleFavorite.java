package com.birdy.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章收藏实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("bg_article_favorite")
public class ArticleFavorite {
    
    /**
     * 文章ID（联合主键）
     */
    @TableField("article_id")
    private Long articleId;
    
    /**
     * 用户ID（联合主键）
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
    
    public ArticleFavorite(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
    }
}