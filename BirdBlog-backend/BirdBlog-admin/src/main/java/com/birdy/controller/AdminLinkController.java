package com.birdy.controller;


import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.PageResult;
import com.birdy.domain.entity.Link;

import com.birdy.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 友链管理 Controller
 *
 * @author Young
 * @date 2025/11/08
 * @description 友链管理接口，包括分页查询、新增、修改、删除等操作
 */
@RestController
@RequestMapping("/links")
public class AdminLinkController {

    @Autowired
    private LinkService linkService;

    /**
     * 获取友链分页列表（支持按名称模糊查询和状态筛选）
     *
     * @param pageNum 页码，默认第1页
     * @param pageSize 每页数量，默认10条
     * @param name 友链名称，支持模糊查询，可选参数
     * @param status 审核状态：0通过 1未通过 2待审核，可选参数
     * @return 分页结果，包含友链列表和分页信息
     */
    @GetMapping("/page")
    public CommonResult<PageResult<Link>> getPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status
    ){
        try{
            PageResult<Link> pageResult = linkService.getPageListWithQuery(pageNum, pageSize, name, status);
            return CommonResult.success(pageResult);
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.error(
                    com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                    "获取友链列表失败: " + e.getMessage()
            );
        }
    }



    /**
     * 获取友链详情
     */
    @GetMapping("/{id}")
    public CommonResult<?> getById(@PathVariable Long id) {
        return linkService.getDetail(id);
    }

    /**
     * 添加友链
     *
     * @param link 友链信息
     * @return 添加结果
     */
    @PostMapping
    public CommonResult<String> create(@RequestBody Link link) {
        try {
            return linkService.addLink(link);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "添加友链失败: " + e.getMessage()
            );
        }
    }

    /**
     * 修改友链
     *
     * @param id 友链ID
     * @param link 友链信息
     * @return 修改结果
     */
    @PutMapping("/{id}")
    public CommonResult<String> update(@PathVariable Long id, @RequestBody Link link) {
        try {
            // 设置友链ID
            link.setId(id);
            return linkService.updateLink(link);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "修改友链失败: " + e.getMessage()
            );
        }
    }

    /**
     * 删除友链
     *
     * @param ids 友链ID，多个ID用逗号分隔
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    public CommonResult<String> delete(@PathVariable String ids) {
        try {
            // 将字符串转换为List<Long>
            java.util.List<Long> idList = java.util.Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(java.util.stream.Collectors.toList());

            if (idList.isEmpty()) {
                return CommonResult.error(
                    com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                    "请提供要删除的友链ID"
                );
            }

            return linkService.deleteLinks(idList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "友链ID格式不正确"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "删除友链失败: " + e.getMessage()
            );
        }
    }


}
