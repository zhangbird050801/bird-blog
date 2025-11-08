package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.ArticleQueryDTO;
import com.birdy.domain.vo.AdminArticleVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Birdy
 * @date 2025/11/2 18:11
 * @description AdminArticleController
 */
@RestController
@RequestMapping("/article")
public class AdminArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 分页查询文章列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public CommonResult<PageResult<AdminArticleVO>> getArticleList(ArticleQueryDTO queryDTO) {
        return articleService.getArticleList(queryDTO);
    }

    @PostMapping
    public CommonResult<?> add() {
        return CommonResult.success();
    }

    @PutMapping("/{id}")
    public CommonResult<?> update() {
        return CommonResult.success();
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete() {
        return CommonResult.success();
    }

}
