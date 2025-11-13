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

    /**
     * 新增分类
     * @param category 分类信息
     * @return 新增结果
     */
    CommonResult<String> addCategory(Category category);

    /**
     * 更新分类
     * @param category 分类信息
     * @return 更新结果
     */
    CommonResult<String> updateCategory(Category category);

    /**
     * 删除分类
     * @param ids 分类ID列表
     * @return 删除结果
     */
    CommonResult<String> deleteCategories(List<Long> ids);

    /**
     * 获取分类详情
     * @param id 分类ID
     * @return 分类详情
     */
    CommonResult<Category> getCategoryDetail(Long id);
}
