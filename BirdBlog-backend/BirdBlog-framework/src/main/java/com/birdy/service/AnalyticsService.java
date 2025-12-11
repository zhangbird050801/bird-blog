package com.birdy.service;

import com.birdy.domain.CommonResult;

/**
 * 数据看板统计服务
 */
public interface AnalyticsService {

    /**
        * 热门文章排行榜
        */
    CommonResult getHotArticles(int limit);

    /**
     * 互动（点赞+收藏）排行榜
     */
    CommonResult getEngagementRank(int limit);

    /**
     * 评论排行榜
     */
    CommonResult getCommentRank(int limit);

    /**
     * 数据看板总览
     */
    CommonResult getOverview(int categoryLimit);

    /**
     * 标签云数据
     */
    CommonResult getTagCloud(int limit);
}
