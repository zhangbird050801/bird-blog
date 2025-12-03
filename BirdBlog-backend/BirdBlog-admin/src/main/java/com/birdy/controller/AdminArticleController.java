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

import java.util.Map;

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
        return articleService.createArticle(
                article,
                articleVO.getTagIds(),
                articleVO.getNewTags(),
                articleVO.getNewTagsDetail(),
                articleVO.getNewTagRemark(),
                articleVO.getNewCategoryName(),
                articleVO.getNewCategoryRemark()
        );
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
        return articleService.updateArticle(
                article,
                articleVO.getTagIds(),
                articleVO.getNewTags(),
                articleVO.getNewTagsDetail(),
                articleVO.getNewTagRemark(),
                articleVO.getNewCategoryName(),
                articleVO.getNewCategoryRemark()
        );
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

    /**
     * 发布文章
     *
     * @param id 文章ID
     */
    @PutMapping("/{id}/publish")
    public CommonResult<Void> publishArticle(@PathVariable Long id) {
        return articleService.publishArticle(id);
    }

    /**
     * 将文章设为草稿
     *
     * @param id 文章ID
     */
    @PutMapping("/{id}/draft")
    public CommonResult<Void> draftArticle(@PathVariable Long id) {
        return articleService.draftArticle(id);
    }

    /**
     * 置顶/取消置顶
     *
     * @param id 文章ID
     * @param body 请求体，包含 isTop
     */
    @PutMapping("/{id}/top")
    public CommonResult<Void> toggleTop(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Object flag = body.get("isTop");
        if (flag == null) {
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.PARAM_ERROR, "isTop 不能为空");
        }
        boolean isTop = Boolean.TRUE.equals(flag) || "true".equalsIgnoreCase(flag.toString());
        return articleService.toggleTop(id, isTop);
    }
}
