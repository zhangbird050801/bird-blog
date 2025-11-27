package com.birdy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.entity.ArticleLike;
import com.birdy.mapper.ArticleLikeMapper;
import com.birdy.mapper.ArticleMapper;
import com.birdy.service.ArticleLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文章点赞服务实现类
 *
 * @author birdy
 */
@Service
public class ArticleLikeServiceImpl extends ServiceImpl<ArticleLikeMapper, ArticleLike> implements ArticleLikeService {

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeArticle(Long articleId, Long userId) {
        // 检查是否已点赞
        ArticleLike existingLike = articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
        if (existingLike != null) {
            return false; // 已点赞过
        }

        // 创建新的点赞记录
        ArticleLike articleLike = new ArticleLike();
        articleLike.setArticleId(articleId);
        articleLike.setUserId(userId);

        // 保存点赞记录
        int insertCount = articleLikeMapper.insert(articleLike);
        if (insertCount > 0) {
            // 更新文章的点赞数
            articleMapper.incrementLikeCount(articleId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikeArticle(Long articleId, Long userId) {
        // 检查是否有点赞记录
        ArticleLike existingLike = articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
        if (existingLike == null) {
            return false; // 未点赞过
        }

        // 删除点赞记录
        int deleteCount = articleLikeMapper.deleteByArticleIdAndUserId(articleId, userId);
        if (deleteCount > 0) {
            // 更新文章的点赞数
            articleMapper.decrementLikeCount(articleId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLiked(Long articleId, Long userId) {
        ArticleLike articleLike = articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
        return articleLike != null;
    }

    @Override
    public Long getLikeCount(Long articleId) {
        return articleLikeMapper.countByArticleId(articleId);
    }

    @Override
    public ArticleLike getLikeByArticleIdAndUserId(Long articleId, Long userId) {
        return articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
    }
}