package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.vo.*;
import com.birdy.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 获取热门文章
     * @return 热门文章列表
     */
    @GetMapping("/hot")
    public CommonResult<List<HotArticleVO>> hot() {
        return articleService.hot();
    }

    /**
     * 获取文章列表
     * @param categoryId 分类id
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 文章列表
     */
    @GetMapping("/list")
    public CommonResult<PageResult<ArticleVO>> list(Long categoryId, int pageNum, int pageSize) {
        return articleService.list(categoryId, pageNum, pageSize);
    }

    /**
     * 文章详情
     *   GET /article/10001                 -> 通过 ID 查询
     *   GET /article/springboot-quickstart -> 通过 slug 查询
     * 
     * @param idOrSlug 文章ID 或 slug
     * @return 文章详情
     */
    @GetMapping("/{idOrSlug}")
    public CommonResult<ArticleDetailVO> getDetail(@PathVariable String idOrSlug) {
        if (idOrSlug.matches("\\d+")) {
            // 纯数字按ID查询
            return articleService.getDetail(Long.parseLong(idOrSlug));
        } else {
            return articleService.getDetailBySlug(idOrSlug);
        }
    }

    /**
     * 获取最新文章
     * @return 最新文章列表 3 篇
     */
    @GetMapping("/latest")
    public CommonResult<List<LatestArticleVO>> latest() {
        return articleService.latest();
    }
}
