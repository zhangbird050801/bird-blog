package com.birdy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.birdy.domain.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章点赞 Mapper 接口
 *
 * @author birdy
 */
@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLike> {

    /**
     * 根据文章ID和用户ID查询点赞记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    ArticleLike selectByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 根据文章ID和用户ID删除点赞记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 统计文章的点赞数量
     *
     * @param articleId 文章ID
     * @return 点赞数量
     */
    Long countByArticleId(@Param("articleId") Long articleId);
}