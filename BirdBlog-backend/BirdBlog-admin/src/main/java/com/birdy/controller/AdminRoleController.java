package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.RoleCreateDTO;
import com.birdy.domain.dto.RoleQueryDTO;
import com.birdy.domain.dto.RoleUpdateDTO;
import com.birdy.domain.dto.RoleStatusUpdateDTO;
import com.birdy.domain.dto.RoleMenuUpdateDTO;
import com.birdy.domain.vo.RoleVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.domain.vo.MenuVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 角色管理 Controller
 *
 * @author Birdy
 * @date 2025/11/21
 * @description 角色管理接口
 */
@RestController
@RequestMapping("/roles")
public class AdminRoleController {

    @Autowired
    private SysRoleService roleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping("/page")
    public CommonResult<PageResult<RoleVO>> getPage(RoleQueryDTO queryDTO) {
        return roleService.getRolePage(queryDTO);
    }

    /**
     * 获取所有角色列表（不分页，用于下拉选择）
     */
    @GetMapping("/list")
    public CommonResult<List<RoleVO>> getList() {
        return roleService.getAllRoles();
    }

    /**
     * 创建角色
     */
    @PostMapping
    public CommonResult<Void> create(@RequestBody RoleCreateDTO createDTO) {
        // 验证角色编码是否已存在
        if (roleService.existsByCode(createDTO.getCode())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "角色编码已存在");
        }

        return roleService.createRole(createDTO);
    }

    /**
     * 更新角色信息
     */
    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody RoleUpdateDTO updateDTO) {
        // 检查角色是否存在
        if (!roleService.existsById(id)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "角色不存在");
        }

        // 如果修改了编码，检查新编码是否已被其他角色使用
        if (!Objects.equals(roleService.getById(id).getCode(), updateDTO.getCode())) {
            if (roleService.existsByCode(updateDTO.getCode())) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "角色编码已存在");
            }
        }

        return roleService.updateRole(id, updateDTO);
    }

    /**
     * 更新角色状态
     */
    @PutMapping("/{id}/status")
    public CommonResult<Void> updateStatus(@PathVariable Long id, @RequestBody RoleStatusUpdateDTO statusDTO) {
        if (statusDTO == null || statusDTO.getStatus() == null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "状态不能为空");
        }

        // 检查角色是否存在
        if (!roleService.existsById(id)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "角色不存在");
        }

        return roleService.updateRoleStatus(id, statusDTO.getStatus());
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        // 检查角色是否存在
        if (!roleService.existsById(id)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "角色不存在");
        }

        // 检查是否有用户使用此角色
        if (roleService.hasUsers(id)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "该角色下存在用户，无法删除");
        }

        return roleService.deleteRole(id);
    }

    /**
     * 获取菜单树（用于权限分配）
     */
    @GetMapping("/menus/tree")
    public CommonResult<List<MenuVO>> getMenuTree() {
        return roleService.getMenuTree();
    }

    /**
     * 获取角色的菜单权限ID列表
     */
    @GetMapping("/{roleId}/menu-ids")
    public CommonResult<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        // 检查角色是否存在
        if (!roleService.existsById(roleId)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "角色不存在");
        }

        return roleService.getRoleMenuIds(roleId);
    }

    /**
     * 更新角色菜单权限
     */
    @PutMapping("/{roleId}/menus")
    public CommonResult<Void> updateRoleMenus(@PathVariable Long roleId, @RequestBody RoleMenuUpdateDTO roleMenuDTO) {
        // 检查角色是否存在
        if (!roleService.existsById(roleId)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "角色不存在");
        }

        return roleService.updateRoleMenus(roleId, roleMenuDTO.getMenuIds());
    }
}