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
}
