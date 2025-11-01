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
}
