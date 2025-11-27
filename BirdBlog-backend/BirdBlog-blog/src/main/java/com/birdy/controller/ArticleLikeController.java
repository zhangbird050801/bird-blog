package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.ArticleLike;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.service.ArticleLikeService;
import com.birdy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 文章点赞控制器
 *
 * @author birdy
 */
@RestController
@RequestMapping("/article/like")
public class ArticleLikeController {

    @Autowired
    private ArticleLikeService articleLikeService;

    /**
     * 点赞文章
     */
    @PostMapping("/{articleId}")
    @PreAuthorize("hasRole('admin') or hasRole('user')")
    public CommonResult<Boolean> likeArticle(@PathVariable Long articleId) {

        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
        }

        boolean result = articleLikeService.likeArticle(articleId, userId);
        if (result) {
            return CommonResult.success(result);
        } else {
            return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "您已经点赞过该文章");
        }
    }

    /**
     * 取消点赞文章
     */
    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasRole('admin') or hasRole('user')")
    public CommonResult<Boolean> unlikeArticle(@PathVariable Long articleId) {

        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
        }

        boolean result = articleLikeService.unlikeArticle(articleId, userId);
        if (result) {
            return CommonResult.success(result);
        } else {
            return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "您还未点赞过该文章");
        }
    }

    /**
     * 检查用户是否点赞过文章
     */
    @GetMapping("/status/{articleId}")
    @PreAuthorize("hasRole('admin') or hasRole('user')")
    public CommonResult<Boolean> checkLikeStatus(@PathVariable Long articleId) {

        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
        }

        boolean isLiked = articleLikeService.isLiked(articleId, userId);
        return CommonResult.success(isLiked);
    }

    /**
     * 获取文章的点赞数量
     */
    @GetMapping("/count/{articleId}")
    public CommonResult<Long> getLikeCount(@PathVariable Long articleId) {

        Long likeCount = articleLikeService.getLikeCount(articleId);
        return CommonResult.success(likeCount);
    }
}