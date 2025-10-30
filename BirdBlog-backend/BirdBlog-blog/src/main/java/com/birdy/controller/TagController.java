package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.vo.TagVO;
import com.birdy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签控制器
 * 提供标签相关的API接口
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    
    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签列表
     * @return 标签列表
     */
    @GetMapping("/list")
    public CommonResult<List<TagVO>> getAllTags() {
        return tagService.getAllTags();
    }

    /**
     * 根据文章ID获取该文章的所有标签
     * @param articleId 文章ID
     * @return 标签列表
     */
    @GetMapping("/article/{articleId}")
    public CommonResult<List<TagVO>> getTagsByArticleId(@PathVariable("articleId") Long articleId) {
        return tagService.getTagsByArticleId(articleId);
    }
}
