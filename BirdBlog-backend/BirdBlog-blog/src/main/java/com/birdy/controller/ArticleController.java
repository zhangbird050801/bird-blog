package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 获取热门文章
     * @return
     */
    @GetMapping("/hot")
    public CommonResult hot() {
        return articleService.hot();
    }

    /**
     * 获取文章列表
     * @param categoryId 分类id
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return
     */
    @GetMapping("/list")
    public CommonResult list(Long categoryId, int pageNum, int pageSize) {
        return articleService.list(categoryId, pageNum, pageSize);
    }
}
