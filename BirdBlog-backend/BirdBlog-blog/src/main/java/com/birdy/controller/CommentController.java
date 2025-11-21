package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.AddCommentRequest;
import com.birdy.domain.vo.CommentVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.service.CommentService;
import com.birdy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 * 提供评论相关的API接口
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取文章评论列表
     * @param articleId 文章ID
     * @return 评论列表（树形结构）
     */
    @GetMapping("/article/{articleId}")
    public CommonResult<List<CommentVO>> getArticleComments(@PathVariable("articleId") Long articleId) {
        return commentService.getArticleComments(articleId);
    }

    /**
     * 获取友链评论列表
     * @param linkId 友链ID
     * @return 评论列表（树形结构）
     */
    @GetMapping("/link/{linkId}")
    public CommonResult<List<CommentVO>> getLinkComments(@PathVariable("linkId") Long linkId) {
        return commentService.getLinkComments(linkId);
    }

    /**
     * 添加评论
     * @param request 评论请求
     * @return 添加结果
     */
    @PostMapping("/add")
    public CommonResult<Void> addComment(@RequestBody AddCommentRequest request) {
        // 检查用户是否登录
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return CommonResult.error(HttpCodeEnum.NEED_LOGIN, "请先登录后再评论");
        }

        return commentService.addComment(request, userId);
    }
}
