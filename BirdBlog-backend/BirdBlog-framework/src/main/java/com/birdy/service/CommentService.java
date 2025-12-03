package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.AddCommentRequest;
import com.birdy.domain.entity.Comment;
import com.birdy.domain.vo.CommentVO;
import com.birdy.domain.vo.PageResult;
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
     * 获取友链评论列表（树形结构）
     * @param linkId 友链ID
     * @return 评论列表
     */
    CommonResult<List<CommentVO>> getLinkComments(Long linkId);

    /**
     * 添加评论
     * @param request 评论请求
     * @param userId 当前登录用户ID
     * @return 添加结果
     */
    CommonResult<Void> addComment(AddCommentRequest request, Long userId);

    /**
     * 分页查询评论列表（管理员使用）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param type 评论类型：0文章评论，1友链评论
     * @param articleId 文章ID（可选）
     * @param status 状态：0正常，1屏蔽
     * @param content 评论内容（模糊查询）
     * @param objectKeyword 关联对象关键词（文章标题或友链名称，模糊查询），可选参数
     * @return 分页结果
     */
    PageResult<CommentVO> getPageList(Integer pageNum, Integer pageSize, Integer type, Long articleId, Integer status, String content, String objectKeyword);

    /**
     * 更新评论状态
     * @param id 评论ID
     * @param status 状态：0正常，1屏蔽
     * @return 操作结果
     */
    CommonResult<Void> updateStatus(Long id, Integer status);

    /**
     * 批量删除评论
     * @param ids 评论ID列表，逗号分隔
     * @return 操作结果
     */
    CommonResult<Void> deleteComments(String ids);

    /**
     * 获取单条评论详情（包含用户与关联对象信息）
     * @param id 评论ID
     * @return 评论详情
     */
    CommonResult<CommentVO> getDetail(Long id);
}
