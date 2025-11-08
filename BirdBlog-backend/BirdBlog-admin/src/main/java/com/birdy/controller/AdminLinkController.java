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
     * 暂未实现
     */
    @GetMapping("/{id}")
    public CommonResult<?> getById(@PathVariable Long id) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "友链详情功能暂未实现");
    }

    /**
     * 添加友链
     * 暂未实现
     */
    @PostMapping
    public CommonResult<?> create(@RequestBody Link link) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "添加友链功能暂未实现");
    }

    /**
     * 修改友链
     * 暂未实现
     */
    @PutMapping("/{id}")
    public CommonResult<?> update(@PathVariable Long id, @RequestBody Link link) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "修改友链功能暂未实现");
    }

    /**
     * 删除友链
     * 暂未实现
     */
    @DeleteMapping("/{ids}")
    public CommonResult<?> delete(@PathVariable String ids) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "删除友链功能暂未实现");
    }


}