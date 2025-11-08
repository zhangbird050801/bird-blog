package com.birdy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.birdy.domain.vo.CategoryVO;
import com.birdy.domain.vo.PageResult;

import java.util.List;

/**
* @author Young
* @description 针对表【bg_category(分类表)】的数据库操作Service
* @createDate 2025-10-29 01:00:54
*/
public interface CategoryService extends IService<Category> {

    /**
     * 获取分类列表（博客前台使用）
     * @return 分类列表
     */
    CommonResult<List<CategoryVO>> get();

    /**
     * 获取分页分类列表（管理后台使用）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<Category> getPageList(Integer pageNum, Integer pageSize);

    /**
     * 获取分页分类列表（支持模糊查询和状态筛选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param name 分类名称（支持模糊查询）
     * @param status 状态筛选
     * @return 分页结果
     */
    PageResult<Category> getPageListWithQuery(Integer pageNum, Integer pageSize, String name, Integer status);
}
