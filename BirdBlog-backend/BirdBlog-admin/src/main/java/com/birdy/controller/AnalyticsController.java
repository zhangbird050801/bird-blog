package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作台数据看板
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * 热门文章（按浏览量排序）
     */
    @GetMapping("/hot-articles")
    public CommonResult getHotArticles(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return analyticsService.getHotArticles(limit);
    }

    /**
     * 收藏/点赞互动排行
     */
    @GetMapping("/engagement-rank")
    public CommonResult getEngagementRank(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return analyticsService.getEngagementRank(limit);
    }

    /**
     * 评论排行榜
     */
    @GetMapping("/comment-rank")
    public CommonResult getCommentRank(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return analyticsService.getCommentRank(limit);
    }

    /**
     * 总览数据（含分类占比）
     */
    @GetMapping("/overview")
    public CommonResult getOverview(@RequestParam(value = "categoryLimit", defaultValue = "6") Integer categoryLimit) {
        return analyticsService.getOverview(categoryLimit);
    }

    /**
     * 标签云
     */
    @GetMapping("/tag-cloud")
    public CommonResult getTagCloud(@RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return analyticsService.getTagCloud(limit);
    }
}
