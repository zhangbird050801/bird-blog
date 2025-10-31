package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜单管理 Controller
 *
 * @author Birdy
 * @date 2025/10/31
 * @description 菜单路由相关接口
 */
@RestController
@RequestMapping("/menus")
public class AdminMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 获取当前用户的路由列表
     * 前端用于动态生成菜单和路由
     *
     * @return 路由列表
     */
    @GetMapping("/routes")
    public CommonResult<List<Map<String, Object>>> getRoutes() {
        // 从数据库查询菜单并构建路由树
        List<Map<String, Object>> routes = sysMenuService.getRoutes();
        return CommonResult.success(routes);
    }

    /**
     * 获取菜单树形列表
     * 暂未实现
     */
    @GetMapping
    public CommonResult<?> getList() {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "菜单管理功能暂未实现");
    }

    /**
     * 获取菜单下拉数据源
     * 暂未实现
     */
    @GetMapping("/options")
    public CommonResult<?> getOptions() {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "菜单管理功能暂未实现");
    }

    /**
     * 获取菜单表单数据
     * 暂未实现
     */
    @GetMapping("/{id}/form")
    public CommonResult<?> getFormData(@PathVariable Long id) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "菜单管理功能暂未实现");
    }

    /**
     * 新增菜单
     * 暂未实现
     */
    @PostMapping
    public CommonResult<?> create(@RequestBody Object data) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "菜单管理功能暂未实现");
    }

    /**
     * 修改菜单
     * 暂未实现
     */
    @PutMapping("/{id}")
    public CommonResult<?> update(@PathVariable Long id, @RequestBody Object data) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "菜单管理功能暂未实现");
    }

    /**
     * 删除菜单
     * 暂未实现
     */
    @DeleteMapping("/{ids}")
    public CommonResult<?> delete(@PathVariable String ids) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "菜单管理功能暂未实现");
    }
}
