package com.birdy.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 管理员文章列表 VO
 *
 * @author Birdy
 * @date 2025/11/8
 * @description 管理后台文章列表返回数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminArticleVO {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * URL别名
     */
    private String slug;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 正文内容
     */
    private String content;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 缩略图URL
     */
    private String thumbnail;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 状态：0已发布，1草稿
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 允许评论
     */
    private Boolean isComment;

    /**
     * 浏览量
     */
    private Long viewCount;

    /**
     * 点赞量
     */
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishedTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 绑定的标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 新增的标签名称列表（仅创建/更新时使用）
     */
    private List<String> newTags;

    /**
     * 新增标签详情（名称+备注）
     */
    private List<com.birdy.domain.dto.TagCreateDTO> newTagsDetail;
    /**
     * 新增标签备注（可选，作用于本次所有新建标签）
     */
    private String newTagRemark;

    /**
     * 新增分类名称（仅创建/更新时使用）
     */
    private String newCategoryName;

    /**
     * 新增分类备注（可选）
     */
    private String newCategoryRemark;

    /**
     * 显式提供分类/作者 setter，避免 Lombok 处理异常影响编译
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (this.status == null) {
            return "未知";
        }
        return this.status == 0 ? "已发布" : "草稿";
    }
}
