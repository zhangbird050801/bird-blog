package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.MenuFormDTO;
import com.birdy.domain.vo.MenuVO;
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
     *
     * @param name 菜单名称（模糊查询）
     * @param status 状态（0正常 1停用）
     * @return 菜单树形列表
     */
    @GetMapping
    public CommonResult<List<MenuVO>> getList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        List<MenuVO> menuTree = sysMenuService.getMenuTree(name, status);
        return CommonResult.success(menuTree);
    }

    /**
     * 获取菜单下拉数据源（用于选择父菜单）
     *
     * @return 菜单树形选项
     */
    @GetMapping("/options")
    public CommonResult<List<Map<String, Object>>> getOptions() {
        List<Map<String, Object>> options = sysMenuService.getMenuOptions();
        return CommonResult.success(options);
    }

    /**
     * 获取菜单表单数据
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/{id}/form")
    public CommonResult<MenuVO> getFormData(@PathVariable Long id) {
        MenuVO menu = sysMenuService.getMenuById(id);
        if (menu == null) {
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "菜单不存在");
        }
        return CommonResult.success(menu);
    }

    /**
     * 新增菜单
     *
     * @param menuForm 菜单表单
     * @return 是否成功
     */
    @PostMapping
    public CommonResult<Void> create(@RequestBody MenuFormDTO menuForm) {
        boolean success = sysMenuService.addMenu(menuForm);
        return success ? CommonResult.success() : CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "新增菜单失败");
    }

    /**
     * 修改菜单
     *
     * @param id 菜单ID
     * @param menuForm 菜单表单
     * @return 是否成功
     */
    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody MenuFormDTO menuForm) {
        boolean success = sysMenuService.updateMenu(id, menuForm);
        return success ? CommonResult.success() : CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "修改菜单失败");
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        try {
            boolean success = sysMenuService.deleteMenu(id);
            return success ? CommonResult.success() : CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "删除菜单失败");
        } catch (RuntimeException e) {
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, e.getMessage());
        }
    }
}
