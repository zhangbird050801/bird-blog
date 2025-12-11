package com.birdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.ArticleFavorite;
import com.birdy.domain.vo.ArticleVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.mapper.ArticleFavoriteMapper;
import com.birdy.service.ArticleFavoriteService;
import com.birdy.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 文章收藏服务实现类
 */
@Service
public class ArticleFavoriteServiceImpl extends ServiceImpl<ArticleFavoriteMapper, ArticleFavorite> implements ArticleFavoriteService {

    @Override
    public CommonResult toggleFavorite(Long articleId) {
        Long userId = SecurityUtils.getUserId();

        // 查询是否已收藏（包含已逻辑删除记录）
        ArticleFavorite existingFavorite = baseMapper.selectByArticleIdAndUserId(articleId, userId);

        boolean isFavorited;
        if (existingFavorite == null) {
            // 添加收藏
            ArticleFavorite favorite = new ArticleFavorite(articleId, userId);
            favorite.setDeleted(false);
            save(favorite);
            isFavorited = true;
        } else if (Boolean.TRUE.equals(existingFavorite.getDeleted())) {
            // 曾经收藏过但已删除，恢复收藏
            baseMapper.updateDeletedByArticleIdAndUserId(articleId, userId, false);
            isFavorited = true;
        } else {
            // 取消收藏，标记删除
            baseMapper.updateDeletedByArticleIdAndUserId(articleId, userId, true);
            isFavorited = false;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("isFavorited", isFavorited);
        result.put("message", isFavorited ? "收藏成功" : "取消收藏成功");
        
        return CommonResult.success(result);
    }

    @Override
    public CommonResult checkFavoriteStatus(Long articleId) {
        Long userId = SecurityUtils.getUserId();
        
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getArticleId, articleId)
                   .eq(ArticleFavorite::getUserId, userId);
        
        boolean isFavorited = count(queryWrapper) > 0;
        
        Map<String, Object> result = new HashMap<>();
        result.put("isFavorited", isFavorited);
        
        return CommonResult.success(result);
    }

    @Override
    public CommonResult getFavoriteArticles(Integer pageNum, Integer pageSize) {
        Long userId = SecurityUtils.getUserId();
        
        Page<ArticleVO> page = new Page<>(pageNum, pageSize);
        IPage<ArticleVO> articlePage = baseMapper.selectFavoriteArticlesByUserId(page, userId);
        
        PageResult<ArticleVO> pageResult = new PageResult<>(articlePage.getTotal(), articlePage.getRecords(), pageNum, pageSize);
        return CommonResult.success(pageResult);
    }
}
