package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Category;
import com.birdy.domain.vo.PageResult;
import com.birdy.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类管理 Controller
 *
 * @author Birdy
 * @date 2025/11/01
 * @description 分类管理接口，包括分页查询、新增、修改、删除等操作
 */
@RestController
@RequestMapping("/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类分页列表（支持按名称模糊查询和状态筛选）
     *
     * @param pageNum 页码，默认第1页
     * @param pageSize 每页数量，默认10条
     * @param name 分类名称，支持模糊查询，可选参数
     * @param status 状态筛选：0正常，1禁用，可选参数
     * @return 分页结果，包含分类列表和分页信息
     */
    @GetMapping("/page")
    public CommonResult<PageResult<Category>> getPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status
    ) {
        try {
            PageResult<Category> pageResult = categoryService.getPageListWithQuery(pageNum, pageSize, name, status);
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
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public CommonResult<Category> getById(@PathVariable Long id) {
        try {
            return categoryService.getCategoryDetail(id);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "获取分类详情失败: " + e.getMessage()
            );
        }
    }

    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 添加结果
     */
    @PostMapping
    public CommonResult<String> create(@RequestBody Category category) {
        try {
            return categoryService.addCategory(category);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "添加分类失败: " + e.getMessage()
            );
        }
    }

    /**
     * 修改分类
     *
     * @param id 分类ID
     * @param category 分类信息
     * @return 修改结果
     */
    @PutMapping("/{id}")
    public CommonResult<String> update(@PathVariable Long id, @RequestBody Category category) {
        try {
            // 设置分类ID
            category.setId(id);
            return categoryService.updateCategory(category);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "修改分类失败: " + e.getMessage()
            );
        }
    }

    /**
     * 删除分类
     *
     * @param ids 分类ID，多个ID用逗号分隔
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    public CommonResult<String> delete(@PathVariable String ids) {
        try {
            // 将字符串转换为List<Long>
            List<Long> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            if (idList.isEmpty()) {
                return CommonResult.error(
                    com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                    "请提供要删除的分类ID"
                );
            }

            return categoryService.deleteCategories(idList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "分类ID格式不正确"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "删除分类失败: " + e.getMessage()
            );
        }
    }
}