package com.birdy.domain.dto;

import lombok.Data;

/**
 * 添加评论请求 DTO
 *
 * @author Young
 * @date 2025/10/31
 * @description 用户添加评论请求参数
 */
@Data
public class AddCommentRequest {

    /**
     * 文章ID（文章评论必填）
     */
    private Long articleId;

    /**
     * 友链ID（友链评论必填）
     */
    private Long linkId;

    /**
     * 根评论ID（回复评论时填写）
     */
    private Long rootId;

    /**
     * 父评论ID（楼中楼时填写）
     */
    private Long parentId;

    /**
     * 被回复用户ID（回复评论时填写）
     */
    private Long toUserId;

    /**
     * 评论内容（必填）
     */
    private String content;
}




