package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Comment;
import com.birdy.domain.vo.CommentVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评论管理 Controller
 *
 * @author Young
 * @date 2025/11/10
 * @description 评论管理接口，包括分页查询、状态更新、批量删除等操作
 */
@RestController
@RequestMapping("/comments")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取评论分页列表（支持多种筛选条件）
     *
     * @param pageNum 页码，默认第1页
     * @param pageSize 每页数量，默认10条
     * @param type 评论类型：0文章评论，1友链评论，可选参数
     * @param articleId 文章ID或友链ID，可选参数
     * @param status 状态：0正常，1屏蔽，可选参数
     * @param content 评论内容（模糊查询），可选参数
     * @param objectKeyword 关联对象关键词（文章标题或友链名称，模糊查询），可选参数
     * @return 分页结果，包含评论列表和分页信息
     */
    @GetMapping("/page")
    public CommonResult<PageResult<CommentVO>> getPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String objectKeyword
    ) {
        try {
            PageResult<CommentVO> pageResult = commentService.getPageList(pageNum, pageSize, type, articleId, status, content, objectKeyword);
            return CommonResult.success(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "获取评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论详情
     */
    @GetMapping("/{id}")
    public CommonResult<?> getById(@PathVariable Long id) {
        return commentService.getDetail(id);
    }

    /**
     * 更新评论状态
     *
     * @param id 评论ID
     * @param status 状态：0正常，1屏蔽
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public CommonResult<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            return commentService.updateStatus(id, status);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新评论状态失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除评论
     *
     * @param ids 评论ID列表，逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    public CommonResult<Void> deleteComments(@PathVariable String ids) {
        try {
            return commentService.deleteComments(ids);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "删除评论失败: " + e.getMessage());
        }
    }
}
