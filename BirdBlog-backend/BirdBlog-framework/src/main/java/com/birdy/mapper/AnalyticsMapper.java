package com.birdy.mapper;

import com.birdy.domain.vo.ArticleRankVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据看板统计 Mapper
 */
@Mapper
public interface AnalyticsMapper {

    /**
     * 热门文章（按浏览量）
     */
    List<ArticleRankVO> selectHotArticles(@Param("limit") int limit);

    /**
     * 互动排行（点赞+收藏）
     */
    List<ArticleRankVO> selectEngagementRank(@Param("limit") int limit);

    /**
     * 评论排行
     */
    List<ArticleRankVO> selectCommentRank(@Param("limit") int limit);

    /**
     * 总览指标
     */
    java.util.Map<String, Long> selectOverviewMetrics();

    /**
     * 分类浏览占比
     */
    java.util.List<com.birdy.domain.vo.CategoryStatVO> selectCategoryViewShare(@Param("limit") int limit);

    /**
     * 标签使用次数
     */
    java.util.List<com.birdy.domain.vo.TagStatVO> selectTagUsage(@Param("limit") int limit);
}
