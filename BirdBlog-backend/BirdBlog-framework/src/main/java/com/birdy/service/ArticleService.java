package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Article;
import com.birdy.domain.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author birdy
* @description 针对表【bg_article(文章表)】的数据库操作Service
* @createDate 2025-10-24 16:59:24
*/
public interface ArticleService extends IService<Article> {

    /**
     * 获取热门文章列表（按浏览量排序，取前10条）
     */
    CommonResult<List<HotArticleVO>> hot();

    /**
     * 分页查询文章列表
     * 
     * @param categoryId 分类ID（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     */
    CommonResult<PageResult<ArticleVO>> list(Long categoryId, int pageNum, int pageSize);

    /**
     * 获取文章详情（通过ID）
     * 
     * @param id 文章ID
     */
    CommonResult<ArticleDetailVO> getDetail(Long id);

    /**
     * 获取文章详情
     * 
     * @param slug URL别名
     */
    CommonResult<ArticleDetailVO> getDetailBySlug(String slug);

    /**
     * 获取最新文章
     * @return 最新文章 3 篇
     */
    CommonResult<List<LatestArticleVO>> latest();

    /**
     * 返回最多 6 篇相关推荐（基于文章分类）
     * @param articleId 当前文章ID
     * @return 相关推荐文章列表
     */
    CommonResult<List<RelatedArticleVO>> related(Long articleId);

    /**
     * 获取上一篇和下一篇文章
     * @param articleId 当前文章ID
     * @return 上一篇和下一篇文章
     */
    CommonResult<AdjacentArticlesVO> adjacent(Long articleId);
}
