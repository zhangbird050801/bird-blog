package com.birdy.service;

import com.birdy.domain.entity.ArticleLike;

/**
 * 文章点赞服务接口
 *
 * @author birdy
 */
public interface ArticleLikeService {

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return true-点赞成功，false-已点赞过
     */
    boolean likeArticle(Long articleId, Long userId);

    /**
     * 取消点赞文章
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return true-取消成功，false-未点赞过
     */
    boolean unlikeArticle(Long articleId, Long userId);

    /**
     * 检查用户是否点赞过文章
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return true-已点赞，false-未点赞
     */
    boolean isLiked(Long articleId, Long userId);

    /**
     * 获取文章的点赞数量
     *
     * @param articleId 文章ID
     * @return 点赞数量
     */
    Long getLikeCount(Long articleId);

    /**
     * 根据文章ID和用户ID查询点赞记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    ArticleLike getLikeByArticleIdAndUserId(Long articleId, Long userId);
}