package com.birdy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.birdy.domain.entity.ArticleLike;
import com.birdy.domain.vo.ArticleVO;
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
     * 按文章和用户更新删除标记
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @param deleted 删除标记
     * @return 影响的行数
     */
    int updateDeletedByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId, @Param("deleted") Boolean deleted);

    /**
     * 统计文章的点赞数量
     *
     * @param articleId 文章ID
     * @return 点赞数量
     */
    Long countByArticleId(@Param("articleId") Long articleId);

    /**
     * 分页查询用户点赞的文章列表
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 点赞的文章列表
     */
    IPage<ArticleVO> selectLikedArticlesByUserId(Page<ArticleVO> page, @Param("userId") Long userId);
}
