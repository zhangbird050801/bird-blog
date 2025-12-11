package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据看板总览
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsOverviewVO {
    private Long totalArticles;
    private Long totalViews;
    private Long totalLikes;
    private Long totalFavorites;
    private List<CategoryStatVO> categoryViewShare;
}
