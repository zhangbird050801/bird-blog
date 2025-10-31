package com.birdy.constants;

/**
 * @author Birdy
 * @date 2025/10/26 15:16
 * @description SysConstants
 */
public class SysConstants {
    // 文章状态为发布
    public static final int ARTICLE_STATUS_RELEASE = 0;
    // 文章状态为草稿
    public static final int ARTICLE_STATUS_DRAFT = 1;
    // 热门文章查询分页常量
    public static final int HOT_ARTICLE_PAGE_NUM = 1;
    public static final int HOT_ARTICLE_PAGE_SIZE = 5;
    // 最新文章查询分页常量
    public static final int LATEST_ARTICLE_PAGE_NUM = 1;
    public static final int LATEST_ARTICLE_PAGE_SIZE = 3;
    // 分类状态为正常
    public static final int CATEGORY_STATUS_ENABLE = 0;

    //友链审核状态：0已审核通过, 1审核不通过, 2未审核
    public static final int LINK_STATUS_ENABLE = 0;
    public static final int LINK_STATUS_DISABLE = 1;
    public static final int LINK_STATUS_PENDING = 2;

    // 评论类型：0文章评论
    public static final int COMMENT_TYPE_ARTICLE = 0;


    // 评论状态：0正常，1屏蔽
    public static final int COMMENT_STATUS_NORMAL = 0;
    public static final int COMMENT_STATUS_BLOCKED = 1;

}
