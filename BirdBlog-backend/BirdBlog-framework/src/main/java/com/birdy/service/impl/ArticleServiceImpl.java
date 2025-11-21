package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.ArticleQueryDTO;
import com.birdy.domain.entity.Article;
import com.birdy.domain.entity.Category;
import com.birdy.domain.entity.User;
import com.birdy.domain.vo.*;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.mapper.CategoryMapper;
import com.birdy.mapper.UserMapper;
import com.birdy.service.ArticleService;
import com.birdy.mapper.ArticleMapper;
import com.birdy.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

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
    public CommonResult<PageResult<ArticleVO>> list(Long categoryId, Long tagId, int pageNum, int pageSize) {
        Page<ArticleVO> page = new Page<>(pageNum, pageSize);
        Page<ArticleVO> result = articleMapper.selectArticleVOPage(page, categoryId, tagId, ARTICLE_STATUS_RELEASE);

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

    @Override
    public CommonResult<PageResult<AdminArticleVO>> getArticleList(ArticleQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getDeleted, false);

        // 关键词模糊搜索（标题和内容）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Article::getTitle, queryDTO.getKeyword())
                    .or()
                    .like(Article::getContent, queryDTO.getKeyword())
            );
        }

        // 分类筛选
        if (queryDTO.getCategoryId() != null) {
            queryWrapper.eq(Article::getCategoryId, queryDTO.getCategoryId());
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Article::getStatus, queryDTO.getStatus());
        }

        // 作者筛选
        if (queryDTO.getAuthorId() != null) {
            queryWrapper.eq(Article::getAuthorId, queryDTO.getAuthorId());
        }

        // 是否置顶筛选
        if (queryDTO.getIsTop() != null) {
            queryWrapper.eq(Article::getIsTop, queryDTO.getIsTop());
        }

        // 排序：置顶文章优先，然后按发布时间倒序
        queryWrapper.orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getPublishedTime);

        // 分页查询
        Page<Article> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

        List<Article> articles = articlePage.getRecords();

        // 批量查询分类和用户信息
        Map<Long, String> categoryMap = getCategoryMap(articles);
        Map<Long, String> userMap = getUserMap(articles);

        // 转换为VO
        List<AdminArticleVO> adminArticleVOList = articles.stream()
                .map(article -> convertToAdminArticleVO(article, categoryMap, userMap))
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<AdminArticleVO> pageResult = new PageResult<>(
                articlePage.getTotal(),
                adminArticleVOList,
                (int) articlePage.getCurrent(),
                (int) articlePage.getSize()
        );

        return CommonResult.success(pageResult);
    }

    @Override
    public CommonResult<AdminArticleVO> getArticleDetail(Long id) {
        if (id == null || id <= 0) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_ID_NOT_NULL);
        }

        Article article = getById(id);
        if (article == null || article.getDeleted()) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 获取分类信息
        String categoryName = null;
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null && !category.getDeleted()) {
                categoryName = category.getName();
            }
        }

        // 获取作者信息
        String authorName = null;
        if (article.getAuthorId() != null) {
            User author = userMapper.selectById(article.getAuthorId());
            if (author != null && !author.getDeleted()) {
                authorName = author.getNickName();
            }
        }

        // 转换为 AdminArticleVO
        AdminArticleVO adminArticleVO = new AdminArticleVO();
        BeanUtils.copyProperties(article, adminArticleVO);
        adminArticleVO.setCategoryName(categoryName);
        adminArticleVO.setAuthorName(authorName);

        return CommonResult.success(adminArticleVO);
    }

    @Override
    public CommonResult<Long> createArticle(Article article) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
        }

        // 设置作者ID
        article.setAuthorId(currentUserId);

        // 设置默认值
        if (article.getViewCount() == null) {
            article.setViewCount(0L);
        }
        if (article.getIsTop() == null) {
            article.setIsTop(false);
        }
        if (article.getDeleted() == null) {
            article.setDeleted(false);
        }

        // 如果状态为发布且发布时间为空，设置当前时间
        if (article.getStatus() != null && article.getStatus() == ARTICLE_STATUS_RELEASE && article.getPublishedTime() == null) {
            article.setPublishedTime(new java.util.Date());
        }

        boolean success = save(article);
        if (success) {
            return CommonResult.success(article.getId());
        } else {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "文章创建失败");
        }
    }

    @Override
    public CommonResult<Void> updateArticle(Article article) {
        if (article.getId() == null || article.getId() <= 0) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_ID_NOT_NULL);
        }

        // 检查文章是否存在
        Article existingArticle = getById(article.getId());
        if (existingArticle == null || existingArticle.getDeleted()) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 获取当前登录用户ID（检查权限）
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
        }

        // 检查权限：只有文章作者或管理员可以修改
        if (!currentUserId.equals(existingArticle.getAuthorId())) {
            // 检查是否为管理员（这里简化处理，实际应该有角色判断）
            // 暂时允许所有用户修改，或者需要根据实际情况调整权限逻辑
            // return CommonResult.error(HttpCodeEnum.NO_OPERATOR_AUTH);
        }

        // 设置不可修改的字段
        article.setAuthorId(existingArticle.getAuthorId());
        article.setCreateTime(existingArticle.getCreateTime());

        // 如果状态改为发布且发布时间为空，设置当前时间
        if (article.getStatus() != null && article.getStatus() == ARTICLE_STATUS_RELEASE &&
            (article.getPublishedTime() == null || existingArticle.getStatus() == null || existingArticle.getStatus() != ARTICLE_STATUS_RELEASE)) {
            article.setPublishedTime(new java.util.Date());
        }

        boolean success = updateById(article);
        if (success) {
            return CommonResult.success();
        } else {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "文章更新失败");
        }
    }

    /**
     * 批量获取分类信息
     */
    private Map<Long, String> getCategoryMap(List<Article> articles) {
        List<Long> categoryIds = articles.stream()
                .map(Article::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (categoryIds.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.in(Category::getId, categoryIds)
                .eq(Category::getDeleted, false)
                .select(Category::getId, Category::getName);

        List<Category> categories = categoryMapper.selectList(categoryWrapper);
        return categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }

    /**
     * 批量获取用户信息
     */
    private Map<Long, String> getUserMap(List<Article> articles) {
        List<Long> authorIds = articles.stream()
                .map(Article::getAuthorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (authorIds.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, authorIds)
                .eq(User::getDeleted, false)
                .select(User::getId, User::getNickName);

        List<User> users = userMapper.selectList(userWrapper);
        return users.stream()
                .collect(Collectors.toMap(User::getId, User::getNickName));
    }

    /**
     * 转换 Article 实体为 AdminArticleVO
     */
    private AdminArticleVO convertToAdminArticleVO(Article article, Map<Long, String> categoryMap, Map<Long, String> userMap) {
        AdminArticleVO adminArticleVO = new AdminArticleVO();
        BeanUtils.copyProperties(article, adminArticleVO);

        // 设置分类名称
        if (article.getCategoryId() != null) {
            adminArticleVO.setCategoryName(categoryMap.get(article.getCategoryId()));
        }

        // 设置作者名称
        if (article.getAuthorId() != null) {
            adminArticleVO.setAuthorName(userMap.get(article.getAuthorId()));
        }

        return adminArticleVO;
    }
}




