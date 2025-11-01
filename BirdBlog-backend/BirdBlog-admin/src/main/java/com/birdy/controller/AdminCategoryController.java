package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Category;
import com.birdy.domain.vo.PageResult;
import com.birdy.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理 Controller
 *
 * @author Birdy
 * @date 2025/11/01
 * @description 分类管理接口，包括分页查询、新增、修改、删除等操作
 */
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类分页列表
     * 
     * @param pageNum 页码，默认第1页
     * @param pageSize 每页数量，默认10条
     * @return 分页结果，包含分类列表和分页信息
     */
    @GetMapping("/page")
    public CommonResult<PageResult<Category>> getPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        try {
            PageResult<Category> pageResult = categoryService.getPageList(pageNum, pageSize);
            return CommonResult.success(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "获取分类列表失败: " + e.getMessage()
            );
        }
    }

    /**
     * 获取分类详情
     * 暂未实现
     */
    @GetMapping("/{id}")
    public CommonResult<?> getById(@PathVariable Long id) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "分类详情功能暂未实现");
    }

    /**
     * 添加分类
     * 暂未实现
     */
    @PostMapping
    public CommonResult<?> create(@RequestBody Category category) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "添加分类功能暂未实现");
    }

    /**
     * 修改分类
     * 暂未实现
     */
    @PutMapping("/{id}")
    public CommonResult<?> update(@PathVariable Long id, @RequestBody Category category) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "修改分类功能暂未实现");
    }

    /**
     * 删除分类
     * 暂未实现
     */
    @DeleteMapping("/{ids}")
    public CommonResult<?> delete(@PathVariable String ids) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "删除分类功能暂未实现");
    }
}