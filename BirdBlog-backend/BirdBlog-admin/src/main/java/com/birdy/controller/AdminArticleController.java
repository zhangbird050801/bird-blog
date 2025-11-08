package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.ArticleQueryDTO;
import com.birdy.domain.vo.AdminArticleVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.service.AdminArticleService;
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
    private AdminArticleService adminArticleService;

    /**
     * 分页查询文章列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public CommonResult<PageResult<AdminArticleVO>> getArticleList(ArticleQueryDTO queryDTO) {
        return adminArticleService.getArticleList(queryDTO);
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public CommonResult<AdminArticleVO> getArticleDetail(@PathVariable Long id) {
        return adminArticleService.getArticleDetail(id);
    }

    @PostMapping
    public CommonResult<Long> createArticle(@RequestBody AdminArticleVO articleVO) {
        return adminArticleService.createArticle(articleVO);
    }

    @PutMapping("/{id}")
    public CommonResult<Void> updateArticle(@PathVariable Long id, @RequestBody AdminArticleVO articleVO) {
        return adminArticleService.updateArticle(id, articleVO);
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete() {
        return CommonResult.success();
    }

}
