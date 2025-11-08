package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.ArticleQueryDTO;
import com.birdy.domain.vo.AdminArticleVO;
import com.birdy.domain.vo.PageResult;

/**
 * 管理员文章 Service 接口
 *
 * @author Birdy
 * @date 2025/11/8
 * @description 管理后台文章管理接口
 */
public interface AdminArticleService {

    /**
     * 分页查询文章列表（支持模糊搜索）
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    CommonResult<PageResult<AdminArticleVO>> getArticleList(ArticleQueryDTO queryDTO);

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    CommonResult<AdminArticleVO> getArticleDetail(Long id);

    /**
     * 更新文章
     *
     * @param id 文章ID
     * @param articleVO 文章数据
     * @return 更新结果
     */
    CommonResult<Void> updateArticle(Long id, AdminArticleVO articleVO);

    /**
     * 创建文章
     *
     * @param articleVO 文章数据
     * @return 创建结果，返回文章ID
     */
    CommonResult<Long> createArticle(AdminArticleVO articleVO);
}