package com.birdy.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.ArticleLike;
import com.birdy.domain.vo.ArticleVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.mapper.ArticleLikeMapper;
import com.birdy.service.ArticleLikeService;
import com.birdy.utils.SecurityUtils;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeArticle(Long articleId, Long userId) {
        // 检查是否已点赞
        ArticleLike existingLike = articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
        if (existingLike != null && !Boolean.TRUE.equals(existingLike.getDeleted())) {
            return false; // 已点赞过且未删除
        }

        // 如果曾经点过赞但被标记删除，则恢复该记录
        if (existingLike != null && Boolean.TRUE.equals(existingLike.getDeleted())) {
            return articleLikeMapper.updateDeletedByArticleIdAndUserId(articleId, userId, false) > 0;
        }

        // 创建新的点赞记录
        ArticleLike articleLike = new ArticleLike();
        articleLike.setArticleId(articleId);
        articleLike.setUserId(userId);
        articleLike.setDeleted(false);

        // 保存点赞记录
        int insertCount = articleLikeMapper.insert(articleLike);
        return insertCount > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikeArticle(Long articleId, Long userId) {
        // 检查是否有点赞记录
        ArticleLike existingLike = articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
        if (existingLike == null || Boolean.TRUE.equals(existingLike.getDeleted())) {
            return false; // 未点赞过
        }

        // 标记删除点赞记录
        int deleteCount = articleLikeMapper.updateDeletedByArticleIdAndUserId(articleId, userId, true);
        return deleteCount > 0;
    }

    @Override
    public boolean isLiked(Long articleId, Long userId) {
        ArticleLike articleLike = articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
        return articleLike != null && !Boolean.TRUE.equals(articleLike.getDeleted());
    }

    @Override
    public Long getLikeCount(Long articleId) {
        return articleLikeMapper.countByArticleId(articleId);
    }

    @Override
    public ArticleLike getLikeByArticleIdAndUserId(Long articleId, Long userId) {
        return articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
    }

    @Override
    public CommonResult getLikedArticles(Integer pageNum, Integer pageSize) {
        Long userId = SecurityUtils.getUserId();
        
        Page<ArticleVO> page = new Page<>(pageNum, pageSize);
        IPage<ArticleVO> articlePage = articleLikeMapper.selectLikedArticlesByUserId(page, userId);
        
        PageResult<ArticleVO> pageResult = new PageResult<>(articlePage.getTotal(), articlePage.getRecords(), pageNum, pageSize);
        return CommonResult.success(pageResult);
    }
}
