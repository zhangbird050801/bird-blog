package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.ArticleQueryDTO;
import com.birdy.domain.entity.Article;
import com.birdy.domain.entity.Category;
import com.birdy.domain.entity.User;
import com.birdy.domain.entity.ArticleTag;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.*;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.mapper.CategoryMapper;
import com.birdy.mapper.TagMapper;
import com.birdy.mapper.UserMapper;
import com.birdy.mapper.ArticleTagMapper;
import com.birdy.service.ArticleLikeService;
import com.birdy.service.ArticleService;
import com.birdy.mapper.ArticleMapper;
import com.birdy.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
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

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleLikeService articleLikeService;

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

        // 设置点赞状态（如果用户已登录）
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId != null) {
                boolean isLiked = articleLikeService.isLiked(id, userId);
                articleDetail.setIsLiked(isLiked);
            }
        } catch (Exception e) {
            // 用户未登录或其他异常，不设置点赞状态
        }

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

        // 设置点赞状态（如果用户已登录）
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId != null) {
                boolean isLiked = articleLikeService.isLiked(articleDetail.getId(), userId);
                articleDetail.setIsLiked(isLiked);
            }
        } catch (Exception e) {
            // 用户未登录或其他异常，不设置点赞状态
        }

        return CommonResult.success(articleDetail);
    }

    @Override
    public CommonResult<List<LatestArticleVO>> latest() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, ARTICLE_STATUS_RELEASE);
        // 按更新时间降序排序，显示最近更新的文章
        queryWrapper.orderByDesc(Article::getUpdateTime);
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

        // 按更新时间降序排序
        queryWrapper.orderByDesc(Article::getUpdateTime);

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

        // 排序：置顶文章优先，然后按更新时间倒序
        queryWrapper.orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getUpdateTime);

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
        adminArticleVO.setTagIds(getArticleTagIds(id));

        return CommonResult.success(adminArticleVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Long> createArticle(Article article, List<Long> tagIds, List<String> newTags, List<com.birdy.domain.dto.TagCreateDTO> newTagsDetail, String newTagRemark, String newCategoryName, String newCategoryRemark) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
        }

        // 设置作者ID
        article.setAuthorId(currentUserId);

        // 处理分类：如果传入新分类名称则创建/复用并回填 categoryId
        CommonResult<Void> categoryResult = resolveCategory(article, newCategoryName, newCategoryRemark);
        if (categoryResult != null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, categoryResult.getMsg());
        }

        // 处理 slug：生成或规整后校验唯一
        CommonResult<Void> slugCheck = normalizeAndCheckSlug(article, null);
        if (slugCheck != null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, slugCheck.getMsg());
        }

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
        if (!success) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "文章创建失败");
        }

        // 统一创建缺失标签并绑定文章，保证同一事务内一致
        List<Long> finalTagIds = resolveTagIds(tagIds, newTags, newTagsDetail, newTagRemark);
        replaceArticleTags(article.getId(), finalTagIds);
        return CommonResult.success(article.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> updateArticle(Article article, List<Long> tagIds, List<String> newTags, List<com.birdy.domain.dto.TagCreateDTO> newTagsDetail, String newTagRemark, String newCategoryName, String newCategoryRemark) {
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
        // 手动设置更新时间（确保一定会更新）
        article.setUpdateTime(java.time.LocalDateTime.now());

        // 处理分类：如果传入新分类名称则创建/复用并回填 categoryId
        CommonResult<Void> categoryResult = resolveCategory(article, newCategoryName, newCategoryRemark);
        if (categoryResult != null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, categoryResult.getMsg());
        }

        // 只有在以下情况才设置发布时间：
        // 1. 文章状态改为"已发布"
        // 2. 且原来没有发布时间（首次发布）
        // 3. 或原来的状态不是"已发布"（从草稿/待审核改为发布）
        if (article.getStatus() != null && article.getStatus() == ARTICLE_STATUS_RELEASE) {
            // 只有当原文章没有发布时间，或者原文章状态不是已发布时，才设置新的发布时间
            if (existingArticle.getPublishedTime() == null || 
                existingArticle.getStatus() == null || 
                existingArticle.getStatus() != ARTICLE_STATUS_RELEASE) {
                article.setPublishedTime(new java.util.Date());
            } else {
                // 已发布的文章，保持原发布时间不变
                article.setPublishedTime(existingArticle.getPublishedTime());
            }
        }

        // 处理 slug：生成或规整后校验唯一（排除自身）
        CommonResult<Void> slugCheck = normalizeAndCheckSlug(article, article.getId());
        if (slugCheck != null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, slugCheck.getMsg());
        }

        boolean success = updateById(article);
        if (!success) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "文章更新失败");
        }

        // 同步标签（包含新建标签）放在同一事务内
        List<Long> finalTagIds = resolveTagIds(tagIds, newTags, newTagsDetail, newTagRemark);
        replaceArticleTags(article.getId(), finalTagIds);
        return CommonResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> updateArticleTags(Long articleId, List<Long> tagIds) {
        if (articleId == null || articleId <= 0) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_ID_NOT_NULL);
        }
        Article existingArticle = getById(articleId);
        if (existingArticle == null || Boolean.TRUE.equals(existingArticle.getDeleted())) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }
        replaceArticleTags(articleId, tagIds);
        return CommonResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> publishArticle(Long id) {
        // 参数校验
        if (id == null || id <= 0) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_ID_NOT_NULL);
        }
        
        // 检查文章是否存在且未删除
        Article article = getById(id);
        if (article == null || Boolean.TRUE.equals(article.getDeleted())) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 传入 null 作为发布时间，存储过程会自动使用当前时间
        articleMapper.publishArticleByProcedure(id, null);
        
        return CommonResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> draftArticle(Long id) {
        if (id == null || id <= 0) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_ID_NOT_NULL);
        }
        Article article = getById(id);
        if (article == null || Boolean.TRUE.equals(article.getDeleted())) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }
        Article toUpdate = new Article();
        toUpdate.setId(id);
        toUpdate.setStatus(ARTICLE_STATUS_DRAFT);
        boolean success = updateById(toUpdate);
        return success ? CommonResult.success() : CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "设为草稿失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> toggleTop(Long id, Boolean isTop) {
        if (id == null || id <= 0) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_ID_NOT_NULL);
        }
        if (isTop == null) {
            return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "isTop 不能为空");
        }
        Article article = getById(id);
        if (article == null || Boolean.TRUE.equals(article.getDeleted())) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        Article toUpdate = new Article();
        toUpdate.setId(id);
        toUpdate.setIsTop(isTop);
        toUpdate.setPinnedTime(isTop ? new java.util.Date() : null);
        boolean success = updateById(toUpdate);
        return success ? CommonResult.success() : CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "置顶状态更新失败");
    }

    /**
     * 构建最终标签ID集合，包含已选择的标签及需要新建的标签
     */
    private List<Long> resolveTagIds(List<Long> tagIds, List<String> newTagNames, List<com.birdy.domain.dto.TagCreateDTO> newTagsDetail, String newTagRemark) {
        List<Long> resolved = new ArrayList<>();
        if (tagIds != null) {
            resolved.addAll(tagIds.stream().filter(Objects::nonNull).toList());
        }

        List<String> pendingNames = new ArrayList<>();
        if (newTagNames != null) {
            pendingNames.addAll(newTagNames.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .toList());
        }

        // 追加 detail 中的名称，保留对应 remark
        List<com.birdy.domain.dto.TagCreateDTO> detailList = newTagsDetail == null ? List.of() : newTagsDetail;
        for (com.birdy.domain.dto.TagCreateDTO dto : detailList) {
            if (dto != null && StringUtils.hasText(dto.getName())) {
                pendingNames.add(dto.getName().trim());
            }
        }
        // 去重
        pendingNames = pendingNames.stream().filter(StringUtils::hasText).distinct().toList();

        if (!pendingNames.isEmpty()) {
            java.util.Date now = new java.util.Date();
            for (String name : pendingNames) {
                // 若已存在未删除的同名标签则复用
                LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Tag::getName, name)
                        .and(wrapper ->
                                wrapper.isNull(Tag::getDeleted)
                                        .or()
                                        .eq(Tag::getDeleted, false)
                                        .or()
                                        .eq(Tag::getDeleted, 0)
                        );
                Tag existing = tagMapper.selectOne(queryWrapper);
                if (existing != null) {
                    resolved.add(existing.getId());
                    continue;
                }

                String remarkForName = findRemarkForName(detailList, name);
                if (!StringUtils.hasText(remarkForName)) {
                    remarkForName = StringUtils.hasText(newTagRemark) ? newTagRemark.trim() : "";
                }
                Tag tag = new Tag();
                tag.setName(name);
                tag.setRemark(remarkForName);
                tag.setCreator("admin");
                tag.setUpdater("admin");
                tag.setCreateTime(now);
                tag.setUpdateTime(now);
                tag.setDeleted(false);
                tagMapper.insert(tag);
                resolved.add(tag.getId());
            }
        }

        return resolved.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    /**
     * 规范化 slug 并检查唯一性；返回 null 表示通过
     */
    private CommonResult<Void> normalizeAndCheckSlug(Article article, Long excludeId) {
        String originalSlug = article.getSlug();
        if (!StringUtils.hasText(originalSlug)) {
            String generated = generateSlugFromTitle(article.getTitle());
            if (!StringUtils.hasText(generated)) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "slug 不能为空且标题无法生成 slug");
            }
            originalSlug = generated;
        }

        String normalized = normalizeSlug(originalSlug);
        if (!StringUtils.hasText(normalized)) {
            return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "slug 不能为空");
        }

        String uniqueSlug = findUniqueSlug(normalized, excludeId);
        if (uniqueSlug == null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "slug 已存在，请更换后重试");
        }

        article.setSlug(uniqueSlug);
        return null;
    }

    /**
     * 基于标题生成默认 slug
     */
    private String generateSlugFromTitle(String title) {
        if (!StringUtils.hasText(title)) {
            return null;
        }
        String slug = normalizeSlug(title.toLowerCase());
        if (!StringUtils.hasText(slug)) {
            // 避免空 slug，回退到时间戳
            slug = "article-" + System.currentTimeMillis();
        }
        return slug;
    }

    /**
     * 规范化 slug：去除两端连字符，连字符归一
     */
    private String normalizeSlug(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().toLowerCase();
        // 非字母数字全部替换为 "-"
        normalized = normalized.replaceAll("[^a-z0-9]+", "-");
        // 连续 "-" 合并
        normalized = normalized.replaceAll("-{2,}", "-");
        // 去掉首尾 "-"
        normalized = normalized.replaceAll("^-+|-+$", "");
        return normalized;
    }

    /**
     * 在数据库中查找唯一 slug，必要时追加序号
     */
    private String findUniqueSlug(String baseSlug, Long excludeId) {
        String candidate = baseSlug;
        int counter = 1;
        while (slugExists(candidate, excludeId)) {
            candidate = baseSlug + "-" + counter;
            counter++;
            // 防止极端循环
            if (counter > 1000) {
                return null;
            }
        }
        return candidate;
    }

    private boolean slugExists(String slug, Long excludeId) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getSlug, slug);
        if (excludeId != null) {
            queryWrapper.ne(Article::getId, excludeId);
        }
        queryWrapper.eq(Article::getDeleted, false);
        return articleMapper.selectCount(queryWrapper) > 0;
    }

    private String findRemarkForName(List<com.birdy.domain.dto.TagCreateDTO> detailList, String name) {
        if (detailList == null || detailList.isEmpty()) {
            return "";
        }
        for (com.birdy.domain.dto.TagCreateDTO dto : detailList) {
            if (dto == null || dto.getName() == null) {
                continue;
            }
            if (dto.getName().trim().equals(name)) {
                return dto.getRemark() == null ? "" : dto.getRemark().trim();
            }
        }
        return "";
    }

    /**
     * 处理分类：复用已有分类或创建新分类，并回填 article.categoryId
     */
    private CommonResult<Void> resolveCategory(Article article, String newCategoryName, String newCategoryRemark) {
        // 优先使用传入的 categoryId；若缺失且提供了新分类名，则创建
        if (article.getCategoryId() != null) {
            return null;
        }
        if (!StringUtils.hasText(newCategoryName)) {
            return null;
        }
        String name = newCategoryName.trim();
        if (name.isEmpty()) {
            return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "分类名称不能为空");
        }

        // 复用未删除的同名分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, name)
                .and(wrapper ->
                        wrapper.isNull(Category::getDeleted)
                                .or()
                                .eq(Category::getDeleted, false)
                                .or()
                                .eq(Category::getDeleted, 0)
                );
        Category existing = categoryMapper.selectOne(queryWrapper);
        if (existing != null) {
            article.setCategoryId(existing.getId());
            return null;
        }

        Category category = new Category();
        category.setName(name);
        category.setPid(null);
        category.setDescription(StringUtils.hasText(newCategoryRemark) ? newCategoryRemark.trim() : "");
        category.setStatus(SysConstants.CATEGORY_STATUS_ENABLE);
        category.setCount(0);
        category.setCreator("admin");
        category.setUpdater("admin");
        category.setCreateTime(new java.util.Date());
        category.setUpdateTime(new java.util.Date());
        category.setDeleted(false);
        int inserted = categoryMapper.insert(category);
        if (inserted <= 0 || category.getId() == null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "分类创建失败");
        }
        article.setCategoryId(category.getId());
        return null;
    }

    /**
     * 获取文章绑定的标签ID列表
     */
    private List<Long> getArticleTagIds(Long articleId) {
        if (articleId == null) {
            return List.of();
        }
        return articleTagMapper.selectList(
            new LambdaQueryWrapper<ArticleTag>()
                .eq(ArticleTag::getArticleId, articleId)
                .eq(ArticleTag::getDeleted, false)
        ).stream().map(ArticleTag::getTagId).collect(Collectors.toList());
    }

    /**
     * 替换文章标签：软删旧关联，写入新关联
     */
    private void replaceArticleTags(Long articleId, List<Long> tagIds) {
        List<Long> safeTagIds = tagIds == null ? List.of() :
                tagIds.stream().filter(java.util.Objects::nonNull).distinct().collect(Collectors.toList());

        // 软删旧标签
        articleTagMapper.update(null,
                new LambdaUpdateWrapper<ArticleTag>()
                        .eq(ArticleTag::getArticleId, articleId)
                        .set(ArticleTag::getDeleted, true)
                        .set(ArticleTag::getUpdateTime, new java.util.Date())
        );

        if (safeTagIds.isEmpty()) {
            return;
        }

        java.util.Date now = new java.util.Date();
        for (Long tagId : safeTagIds) {
            ArticleTag at = new ArticleTag();
            at.setArticleId(articleId);
            at.setTagId(tagId);
            at.setDeleted(false);
            at.setCreateTime(now);
            at.setUpdateTime(now);
            at.setCreator("system");
            at.setUpdater("system");
            articleTagMapper.insert(at);
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

        // 手动转换 LocalDateTime 到 Date（BeanUtils 不会自动转换类型不匹配的字段）
        if (article.getCreateTime() != null) {
            adminArticleVO.setCreateTime(java.sql.Timestamp.valueOf(article.getCreateTime()));
        }
        if (article.getUpdateTime() != null) {
            adminArticleVO.setUpdateTime(java.sql.Timestamp.valueOf(article.getUpdateTime()));
        }

        return adminArticleVO;
    }
}
