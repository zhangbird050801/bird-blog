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
}