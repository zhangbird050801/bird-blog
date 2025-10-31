package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.AddCommentRequest;
import com.birdy.domain.entity.Comment;
import com.birdy.domain.vo.CommentVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Young
* @description 针对表【bg_comment(评论表)】的数据库操作Service
* @createDate 2025-10-31 02:34:32
*/
public interface CommentService extends IService<Comment> {

    /**
     * 获取文章评论列表（树形结构）
     * @param articleId 文章ID
     * @return 评论列表
     */
    CommonResult<List<CommentVO>> getArticleComments(Long articleId);

    /**
     * 添加评论
     * @param request 评论请求
     * @param userId 当前登录用户ID
     * @return 添加结果
     */
    CommonResult<Void> addComment(AddCommentRequest request, Long userId);
}
