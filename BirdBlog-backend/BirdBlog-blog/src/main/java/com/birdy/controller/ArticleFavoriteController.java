package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.service.ArticleFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文章收藏控制器
 */
@RestController
@RequestMapping("/favorite")
@Tag(name = "文章收藏", description = "文章收藏相关接口")
public class ArticleFavoriteController {

    @Autowired
    private ArticleFavoriteService articleFavoriteService;

    @PostMapping("/toggle/{articleId}")
    @Operation(summary = "收藏/取消收藏文章")
    public CommonResult toggleFavorite(@Parameter(description = "文章ID") @PathVariable("articleId") Long articleId) {
        return articleFavoriteService.toggleFavorite(articleId);
    }

    @GetMapping("/status/{articleId}")
    @Operation(summary = "检查文章收藏状态")
    public CommonResult checkFavoriteStatus(@Parameter(description = "文章ID") @PathVariable("articleId") Long articleId) {
        return articleFavoriteService.checkFavoriteStatus(articleId);
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户收藏的文章列表")
    public CommonResult getFavoriteArticles(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return articleFavoriteService.getFavoriteArticles(pageNum, pageSize);
    }
}