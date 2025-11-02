package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Article;
import com.birdy.domain.vo.*;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.service.ArticleService;
import com.birdy.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.birdy.constants.SysConstants.*;

/**
* @author birdy
* @description 针对表【bg_article(文章表)】的数据库操作Service实现
* @createDate 2025-10-24 16:59:24
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public CommonResult<List<HotArticleVO>> hot() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        // status 状态为已发布
        queryWrapper.eq(Article::getStatus, ARTICLE_STATUS_RELEASE);
        // view_count 浏览量降序
        queryWrapper.orderByDesc(Article::getViewCount);
        // 10 条
        Page<Article> page = new Page<>(HOT_ARTICLE_PAGE_NUM, HOT_ARTICLE_PAGE_SIZE);
        page(page, queryWrapper);

        List<HotArticleVO> res = BeanUtil.copyToList(page.getRecords(), HotArticleVO.class);

        return CommonResult.success(res);
    }

    @Override
    public CommonResult<PageResult<ArticleVO>> list(Long categoryId, int pageNum, int pageSize) {
        Page<ArticleVO> page = new Page<>(pageNum, pageSize);
        Page<ArticleVO> result = articleMapper.selectArticleVOPage(page, categoryId, ARTICLE_STATUS_RELEASE);
        
        // 封装分页结果
        PageResult<ArticleVO> pageResult = new PageResult<>(
            result.getTotal(),
            result.getRecords(),
            pageNum,
            pageSize
        );
        
        return CommonResult.success(pageResult);
    }

    @Override
    public CommonResult<ArticleDetailVO> getDetail(Long id) {
        if (id == null || id <= 0) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_ID_NOT_NULL);
        }

        ArticleDetailVO articleDetail = articleMapper.selectArticleDetailById(id);
        if (articleDetail == null) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 增加浏览量
        articleMapper.incrementViewCount(id);
        return CommonResult.success(articleDetail);
    }

    @Override
    public CommonResult<ArticleDetailVO> getDetailBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_SLUG_NOT_NULL);
        }

        ArticleDetailVO articleDetail = articleMapper.selectArticleDetailBySlug(slug);
        if (articleDetail == null) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        articleMapper.incrementViewCount(articleDetail.getId());
        return CommonResult.success(articleDetail);
    }

    @Override
    public CommonResult<List<LatestArticleVO>> latest() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, ARTICLE_STATUS_RELEASE);
        queryWrapper.orderByDesc(Article::getPublishedTime);
        Page<Article> page = new Page<>(LATEST_ARTICLE_PAGE_NUM, LATEST_ARTICLE_PAGE_SIZE);
        page(page, queryWrapper);

        List<LatestArticleVO> res = BeanUtil.copyToList(page.getRecords(), LatestArticleVO.class);

        return CommonResult.success(res);
    }

    @Override
    public CommonResult<List<RelatedArticleVO>> related(Long articleId) {
        if (articleId == null || articleId <= 0) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_ID_NOT_NULL);
        }

        // 先获取当前文章的分类ID
        Article currentArticle = getById(articleId);
        if (currentArticle == null) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        List<RelatedArticleVO> res = articleMapper.selectRelatedArticles(
                currentArticle.getCategoryId(),
                articleId,
                RELATED_ARTICLE_COUNT);


        return CommonResult.success(res);
    }

    @Override
    public CommonResult<AdjacentArticlesVO> adjacent(Long articleId) {
        if (articleId == null || articleId <= 0) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_ID_NOT_NULL);
        }

        // 先获取当前文章的发布时间
        Article currentArticle = getById(articleId);
        if (currentArticle == null) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 格式化发布时间为字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String publishedTime = sdf.format(currentArticle.getPublishedTime());

        // 查询上一篇和下一篇文章
        AdjacentArticleVO previous = articleMapper.selectPreviousArticle(publishedTime);
        AdjacentArticleVO next = articleMapper.selectNextArticle(publishedTime);

        AdjacentArticlesVO result = new AdjacentArticlesVO(previous, next);
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<List<ArticleVO>> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return CommonResult.error(HttpCodeEnum.KEYWORDS_EMPTY);
        }

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        // 只搜索已发布且未删除的文章
        queryWrapper.eq(Article::getStatus, ARTICLE_STATUS_RELEASE);
        queryWrapper.eq(Article::getDeleted, false);

        // 模糊搜索：标题、摘要、内容
        String searchKeyword = keyword.trim();
        queryWrapper.and(wrapper ->
            wrapper.like(Article::getTitle, searchKeyword)
                   .or()
                   .like(Article::getSummary, searchKeyword)
                   .or()
                   .like(Article::getContent, searchKeyword)
        );

        // 按发布时间降序排序
        queryWrapper.orderByDesc(Article::getPublishedTime);

        List<Article> articles = list(queryWrapper);
        List<ArticleVO> articleVOs = BeanUtil.copyToList(articles, ArticleVO.class);

        return CommonResult.success(articleVOs);
    }
}




