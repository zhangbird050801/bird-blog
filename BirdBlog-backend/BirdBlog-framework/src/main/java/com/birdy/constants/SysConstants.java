package com.birdy.constants;

/**
 * @author Birdy
 * @date 2025/10/26 15:16
 * @description SysConstants
 */
public class SysConstants {
    public static final String SYSTEM = "system";


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
    // 相关文章数量
    public static final int RELATED_ARTICLE_COUNT = 6;


    // 分类状态为正常
    public static final int CATEGORY_STATUS_ENABLE = 0;
    // 分类未删除
    public static final boolean CATEGORY_NOT_DELETED = false;

    // 标签未删除
    public static final boolean TAG_NOT_DELETED = false;


    //友链审核状态：0已审核通过, 1审核不通过, 2未审核
    public static final int LINK_STATUS_ENABLE = 0;
    public static final int LINK_STATUS_DISABLE = 1;
    public static final int LINK_STATUS_PENDING = 2;


    // 评论类型：0文章评论
    public static final int COMMENT_TYPE_ARTICLE = 0;
    // 评论状态：0正常，1屏蔽
    public static final int COMMENT_STATUS_NORMAL = 0;
    public static final int COMMENT_STATUS_BLOCKED = 1;

    // Token 有效期 (秒) 7 天
    public static final long REFRESH_TOKEN_TTL_SECONDS = 7L * 24 * 60 * 60;

    // 密码最小长度
    public static final int PASSWORD_MIN_LENGTH = 6;

    // 用户相关默认值
    public static final int USER_TYPE_NORMAL = 0;
    public static final int USER_STATUS_NORMAL = 0;
    public static final int USER_SEX_UNKNOWN = 2;
    // 用户状态非禁用
    public static final boolean USER_NOT_DELETED = false;
}
