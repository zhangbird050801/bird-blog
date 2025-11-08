package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.PageResult;
import com.birdy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 标签管理 Controller
 *
 * @author Young
 * @date 2025/11/01
 * @description 标签管理接口，包括分页查询、新增、修改、删除等操作
 */
@RestController
@RequestMapping("/tags")
public class AdminTagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取标签分页列表（支持按名称模糊查询）
     *
     * @param pageNum 页码，默认第1页
     * @param pageSize 每页数量，默认10条
     * @param name 标签名称，支持模糊查询，可选参数
     * @return 分页结果，包含标签列表和分页信息
     */
    @GetMapping("/page")
    public CommonResult<PageResult<Tag>> getPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name
    ) {
        try {
            PageResult<Tag> pageResult = tagService.getPageListWithQuery(pageNum, pageSize, name);
            return CommonResult.success(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "获取标签列表失败: " + e.getMessage()
            );
        }
    }

    /**
     * 获取标签详情
     * 暂未实现
     */
    @GetMapping("/{id}")
    public CommonResult<?> getById(@PathVariable Long id) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "标签详情功能暂未实现");
    }

    /**
     * 添加标签
     * 暂未实现
     */
    @PostMapping
    public CommonResult<?> create(@RequestBody Tag tag) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "添加标签功能暂未实现");
    }

    /**
     * 修改标签
     * 暂未实现
     */
    @PutMapping("/{id}")
    public CommonResult<?> update(@PathVariable Long id, @RequestBody Tag tag) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "修改标签功能暂未实现");
    }

    /**
     * 删除标签
     * 暂未实现
     */
    @DeleteMapping("/{ids}")
    public CommonResult<?> delete(@PathVariable String ids) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "删除标签功能暂未实现");
    }
}
