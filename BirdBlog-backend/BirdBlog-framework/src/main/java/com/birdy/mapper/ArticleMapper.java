package com.birdy.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.birdy.domain.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.birdy.domain.vo.ArticleDetailVO;
import com.birdy.domain.vo.ArticleVO;
import com.birdy.domain.vo.RelatedArticleVO;
import com.birdy.domain.vo.AdjacentArticleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * @param tagId 标签ID，null表示查询所有标签
     * @param status 文章状态
     * @return 文章VO列表（包含categoryName）
     */
    Page<ArticleVO> selectArticleVOPage(Page<ArticleVO> page,
                                         @Param("categoryId") Long categoryId,
                                         @Param("tagId") Long tagId,
                                         @Param("status") Integer status);

    /**
     * 根据文章ID查询文章详情
     *
     * @param id 文章ID
     * @return 文章详情VO
     */
    ArticleDetailVO selectArticleDetailById(@Param("id") Long id);

    /**
     * 根据 slug 查询文章详情
     *
     * @param slug URL别名
     * @return 文章详情VO
     */
    ArticleDetailVO selectArticleDetailBySlug(@Param("slug") String slug);

    /**
     * 增加文章浏览量
     *
     * @param id 文章ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("id") Long id);

    /**
     * 查询相关文章（基于分类）
     *
     * @param categoryId 分类ID
     * @param excludeArticleId 排除的文章ID
     * @param limit 查询数量限制
     * @return 相关文章列表
     */
    List<RelatedArticleVO> selectRelatedArticles(@Param("categoryId") Long categoryId,
                                                 @Param("excludeArticleId") Long excludeArticleId,
                                                 @Param("limit") Integer limit);

    /**
     * 查询上一篇文章（按发布时间）
     *
     * @param publishedTime 当前文章发布时间
     * @return 上一篇文章
     */
    AdjacentArticleVO selectPreviousArticle(@Param("publishedTime") String publishedTime);

    /**
     * 查询下一篇文章（按发布时间）
     *
     * @param publishedTime 当前文章发布时间
     * @return 下一篇文章
     */
    AdjacentArticleVO selectNextArticle(@Param("publishedTime") String publishedTime);
}




