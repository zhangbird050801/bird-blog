package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.ArticleQueryDTO;
import com.birdy.domain.entity.Article;
import com.birdy.domain.vo.AdminArticleVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.service.ArticleService;
import org.springframework.beans.BeanUtils;
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

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public CommonResult<AdminArticleVO> getArticleDetail(@PathVariable Long id) {
        return articleService.getArticleDetail(id);
    }

    /**
     * 创建文章
     *
     * @param articleVO 文章信息
     * @return 创建结果
     */
    @PostMapping
    public CommonResult<Long> createArticle(@RequestBody AdminArticleVO articleVO) {
        // 转换 VO 为 Entity
        Article article = new Article();
        BeanUtils.copyProperties(articleVO, article);
        return articleService.createArticle(article);
    }

    /**
     * 更新文章
     *
     * @param id 文章ID
     * @param articleVO 文章信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public CommonResult<Void> updateArticle(@PathVariable Long id, @RequestBody AdminArticleVO articleVO) {
        // 转换 VO 为 Entity
        Article article = new Article();
        BeanUtils.copyProperties(articleVO, article);
        article.setId(id);
        return articleService.updateArticle(article);
    }

    /**
     * 删除文章（逻辑删除）
     *
     * @param id 文章ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> deleteArticle(@PathVariable Long id) {
        boolean success = articleService.removeById(id);
        if (success) {
            return CommonResult.success();
        } else {
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "删除失败");
        }
    }
}
