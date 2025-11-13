package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.PageResult;
import com.birdy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
     *
     * @param tag 标签信息
     * @return 添加结果
     */
    @PostMapping
    public CommonResult<String> create(@RequestBody Tag tag) {
        try {
            return tagService.addTag(tag);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "添加标签失败: " + e.getMessage()
            );
        }
    }

    /**
     * 修改标签
     *
     * @param id 标签ID
     * @param tag 标签信息
     * @return 修改结果
     */
    @PutMapping("/{id}")
    public CommonResult<String> update(@PathVariable Long id, @RequestBody Tag tag) {
        try {
            // 设置标签ID
            tag.setId(id);
            return tagService.updateTag(tag);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "修改标签失败: " + e.getMessage()
            );
        }
    }

    /**
     * 删除标签
     *
     * @param ids 标签ID，多个ID用逗号分隔
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
                    com.birdy.enums.HttpCodeEnum.PARAM_ERROR,
                    "请提供要删除的标签ID"
                );
            }

            return tagService.deleteTags(idList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.PARAM_ERROR,
                "标签ID格式不正确"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "删除标签失败: " + e.getMessage()
            );
        }
    }
}
