package com.birdy.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.birdy.domain.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.birdy.domain.vo.ArticleVO;
import org.apache.ibatis.annotations.Param;

/**
* @author birdy
* @description 针对表【bg_article(文章表)】的数据库操作Mapper
* @createDate 2025-10-24 16:59:24
* @Entity com.birdy.domain.entity.Article
*/
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询文章列表（包含分类名称）
     *
     * @param page 分页对象
     * @param categoryId 分类ID，null表示查询所有分类
     * @param status 文章状态
     * @return 文章VO列表（包含categoryName）
     */
    Page<ArticleVO> selectArticleVOPage(Page<ArticleVO> page, 
                                         @Param("categoryId") Long categoryId, 
                                         @Param("status") Integer status);
}




