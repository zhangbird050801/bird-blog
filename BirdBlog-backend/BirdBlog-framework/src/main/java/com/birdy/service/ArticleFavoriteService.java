package com.birdy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.ArticleFavorite;

/**
 * 文章收藏服务接口
 */
public interface ArticleFavoriteService extends IService<ArticleFavorite> {
    
    /**
     * 收藏/取消收藏文章
     */
    CommonResult toggleFavorite(Long articleId);
    
    /**
     * 检查用户是否收藏了文章
     */
    CommonResult checkFavoriteStatus(Long articleId);
    
    /**
     * 获取用户收藏的文章列表
     */
    CommonResult getFavoriteArticles(Integer pageNum, Integer pageSize);
}