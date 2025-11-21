package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.RoleCreateDTO;
import com.birdy.domain.dto.RoleQueryDTO;
import com.birdy.domain.dto.RoleUpdateDTO;
import com.birdy.domain.vo.PageResult;
import com.birdy.domain.vo.RoleVO;
import com.birdy.domain.vo.MenuVO;

import java.util.List;

/**
 * 角色Service接口
 *
 * @author Birdy
 * @date 2025/11/21
 */
public interface SysRoleService {

    /**
     * 分页查询角色列表
     */
    CommonResult<PageResult<RoleVO>> getRolePage(RoleQueryDTO queryDTO);

    /**
     * 获取所有角色列表（不分页，用于下拉选择）
     */
    CommonResult<List<RoleVO>> getAllRoles();

    /**
     * 创建角色
     */
    CommonResult<Void> createRole(RoleCreateDTO createDTO);

    /**
     * 更新角色
     */
    CommonResult<Void> updateRole(Long id, RoleUpdateDTO updateDTO);

    /**
     * 更新角色状态
     */
    CommonResult<Void> updateRoleStatus(Long id, Integer status);

    /**
     * 删除角色
     */
    CommonResult<Void> deleteRole(Long id);

    /**
     * 获取角色菜单权限ID列表
     */
    CommonResult<List<Long>> getRoleMenuIds(Long roleId);

    /**
     * 更新角色菜单权限
     */
    CommonResult<Void> updateRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 获取菜单树（用于权限分配）
     */
    CommonResult<List<MenuVO>> getMenuTree();

    /**
     * 检查角色是否存在
     */
    boolean existsById(Long id);

    /**
     * 检查角色编码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 根据ID获取角色
     */
    RoleVO getById(Long id);

    /**
     * 检查角色是否有用户
     */
    boolean hasUsers(Long id);
}