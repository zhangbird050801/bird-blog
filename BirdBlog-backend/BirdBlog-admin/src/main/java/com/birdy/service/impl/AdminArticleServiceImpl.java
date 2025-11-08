package com.birdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.ArticleQueryDTO;
import com.birdy.domain.entity.Article;
import com.birdy.domain.entity.Category;
import com.birdy.domain.entity.User;
import com.birdy.domain.vo.AdminArticleVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.mapper.ArticleMapper;
import com.birdy.mapper.CategoryMapper;
import com.birdy.mapper.UserMapper;
import com.birdy.service.AdminArticleService;
import com.birdy.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.HashMap;

/**
 * 管理员文章 Service 实现类
 *
 * @author Birdy
 * @date 2025/11/8
 * @description 管理后台文章管理接口实现
 */
@Service
public class AdminArticleServiceImpl implements AdminArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

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
        // 查询文章
        Article article = articleMapper.selectById(id);
        if (article == null || article.getDeleted()) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 查询分类名称
        String categoryName = null;
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null && !category.getDeleted()) {
                categoryName = category.getName();
            }
        }

        // 查询作者名称
        String authorName = null;
        if (article.getAuthorId() != null) {
            User user = userMapper.selectById(article.getAuthorId());
            if (user != null && !user.getDeleted()) {
                authorName = user.getNickName();
            }
        }

        // 转换为VO
        AdminArticleVO adminArticleVO = new AdminArticleVO();
        BeanUtils.copyProperties(article, adminArticleVO);
        adminArticleVO.setCategoryName(categoryName);
        adminArticleVO.setAuthorName(authorName);

        return CommonResult.success(adminArticleVO);
    }

    @Override
    public CommonResult<Void> updateArticle(Long id, AdminArticleVO articleVO) {
        // 查询原文章
        Article existingArticle = articleMapper.selectById(id);
        if (existingArticle == null || existingArticle.getDeleted()) {
            return CommonResult.error(HttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 更新文章信息
        Article updateArticle = new Article();
        BeanUtils.copyProperties(articleVO, updateArticle);
        updateArticle.setId(id);

        // 获取当前登录用户信息
        Long currentUserId = SecurityUtils.getUserId();
        String currentUserInfo = currentUserId != null ? currentUserId.toString() : "admin";

        updateArticle.setUpdater(currentUserInfo);
        updateArticle.setUpdateTime(new java.util.Date());

        // 如果状态改为发布状态且原文章未发布，设置发布时间
        if (updateArticle.getStatus() != null && updateArticle.getStatus() == 0 &&
            (existingArticle.getPublishedTime() == null || existingArticle.getStatus() != 0)) {
            updateArticle.setPublishedTime(new java.util.Date());
        }

        int result = articleMapper.updateById(updateArticle);
        if (result > 0) {
            return CommonResult.success();
        } else {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新文章失败");
        }
    }

    @Override
    public CommonResult<Long> createArticle(AdminArticleVO articleVO) {
        // 创建文章
        Article newArticle = new Article();
        BeanUtils.copyProperties(articleVO, newArticle);

        // 获取当前登录用户信息
        Long currentUserId = SecurityUtils.getUserId();
        String currentUserInfo = currentUserId != null ? currentUserId.toString() : "admin";

        newArticle.setCreator(currentUserInfo);
        newArticle.setAuthorId(currentUserId != null ? currentUserId : 1L); // 如果获取不到用户ID，使用默认值1
        newArticle.setCreateTime(new java.util.Date());
        newArticle.setUpdateTime(new java.util.Date());
        newArticle.setDeleted(false);

        // 设置默认值
        if (newArticle.getViewCount() == null) {
            newArticle.setViewCount(0L);
        }
        if (newArticle.getLikeCount() == null) {
            newArticle.setLikeCount(0L);
        }
        if (newArticle.getCommentCount() == null) {
            newArticle.setCommentCount(0L);
        }

        // 如果是发布状态，设置发布时间
        if (newArticle.getStatus() != null && newArticle.getStatus() == 0) {
            newArticle.setPublishedTime(new java.util.Date());
        }

        int result = articleMapper.insert(newArticle);
        if (result > 0) {
            return CommonResult.success(newArticle.getId());
        } else {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "创建文章失败");
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