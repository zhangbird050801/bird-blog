package com.birdy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.birdy.domain.entity.ArticleFavorite;
import com.birdy.domain.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章收藏Mapper接口
 */
@Mapper
public interface ArticleFavoriteMapper extends BaseMapper<ArticleFavorite> {
    
    /**
     * 分页查询用户收藏的文章列表
     */
    IPage<ArticleVO> selectFavoriteArticlesByUserId(Page<ArticleVO> page, @Param("userId") Long userId);

    /**
     * 根据文章ID和用户ID查询收藏记录（包含已逻辑删除）
     */
    ArticleFavorite selectByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 更新收藏记录的删除标记
     */
    int updateDeletedByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId, @Param("deleted") Boolean deleted);

    /**
     * 统计用户有效的收藏数量（只统计未删除且已发布的文章）
     */
    Long countValidFavoritesByUserId(@Param("userId") Long userId);
}
