package com.birdy.service.impl;

import com.birdy.domain.CommonResult;
import com.birdy.domain.vo.AnalyticsOverviewVO;
import com.birdy.mapper.AnalyticsMapper;
import com.birdy.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据看板统计服务实现
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private AnalyticsMapper analyticsMapper;

    @Override
    public CommonResult getHotArticles(int limit) {
        int size = limit > 0 ? limit : 5;
        return CommonResult.success(analyticsMapper.selectHotArticles(size));
    }

    @Override
    public CommonResult getEngagementRank(int limit) {
        int size = limit > 0 ? limit : 5;
        return CommonResult.success(analyticsMapper.selectEngagementRank(size));
    }

    @Override
    public CommonResult getCommentRank(int limit) {
        int size = limit > 0 ? limit : 5;
        return CommonResult.success(analyticsMapper.selectCommentRank(size));
    }

    @Override
    public CommonResult getOverview(int categoryLimit) {
        int size = categoryLimit > 0 ? categoryLimit : 6;
        AnalyticsOverviewVO vo = new AnalyticsOverviewVO();

        java.util.Map<String, Long> metrics = analyticsMapper.selectOverviewMetrics();
        vo.setTotalArticles(getLong(metrics, "totalArticles"));
        vo.setTotalViews(getLong(metrics, "totalViews"));
        vo.setTotalLikes(getLong(metrics, "totalLikes"));
        vo.setTotalFavorites(getLong(metrics, "totalFavorites"));
        vo.setCategoryViewShare(analyticsMapper.selectCategoryViewShare(size));

        return CommonResult.success(vo);
    }

    @Override
    public CommonResult getTagCloud(int limit) {
        int size = limit > 0 ? limit : 20;
        return CommonResult.success(analyticsMapper.selectTagUsage(size));
    }

    /**
     * 兼容 BigDecimal/Integer/Long 的数值提取
     */
    private Long getLong(java.util.Map<String, ?> map, String key) {
        Object val = map.get(key);
        if (val == null) {
            return 0L;
        }
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        try {
            return Long.parseLong(val.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
