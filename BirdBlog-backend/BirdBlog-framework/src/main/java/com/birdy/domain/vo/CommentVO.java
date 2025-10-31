package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 评论VO - 用于前端展示
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    /**
     * 评论ID
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 根评论ID
     */
    private Long rootId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 评论用户ID
     */
    private Long fromUserId;

    /**
     * 评论用户昵称
     */
    private String fromUserName;

    /**
     * 评论用户头像
     */
    private String fromUserAvatar;

    /**
     * 被回复用户ID
     */
    private Long toUserId;

    /**
     * 被回复用户昵称
     */
    private String toUserName;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 子评论列表
     */
    private List<CommentVO> children;
}




