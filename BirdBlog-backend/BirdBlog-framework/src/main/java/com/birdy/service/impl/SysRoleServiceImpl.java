package com.birdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.RoleCreateDTO;
import com.birdy.domain.dto.RoleQueryDTO;
import com.birdy.domain.dto.RoleUpdateDTO;
import com.birdy.domain.entity.SysRole;
import com.birdy.domain.entity.SysRoleMenu;
import com.birdy.domain.entity.SysMenu;
import com.birdy.domain.entity.SysUserRole;
import com.birdy.domain.vo.PageResult;
import com.birdy.domain.vo.RoleVO;
import com.birdy.domain.vo.MenuVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.mapper.SysRoleMapper;
import com.birdy.mapper.SysRoleMenuMapper;
import com.birdy.mapper.SysMenuMapper;
import com.birdy.mapper.SysUserRoleMapper;
import com.birdy.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public CommonResult<PageResult<RoleVO>> getRolePage(RoleQueryDTO queryDTO) {
        try {
            Page<SysRole> page = new Page<>((long) queryDTO.getPageNum(), (long) queryDTO.getPageSize());
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();

            if (StringUtils.hasText(queryDTO.getKeyword())) {
                String keyword = queryDTO.getKeyword();
                queryWrapper.and(wrapper -> wrapper
                    .like(SysRole::getName, keyword)
                    .or().like(SysRole::getCode, keyword)
                );
            }

            if (queryDTO.getStatus() != null) {
                queryWrapper.eq(SysRole::getStatus, queryDTO.getStatus());
            }

            queryWrapper.eq(SysRole::getDeleted, false);
            queryWrapper.orderByAsc(SysRole::getCreateTime);

            Page<SysRole> rolePage = roleMapper.selectPage(page, queryWrapper);
            List<RoleVO> roleVOList = rolePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

            PageResult<RoleVO> pageResult = new PageResult<>();
            pageResult.setRows(roleVOList);
            pageResult.setTotal(rolePage.getTotal());
            pageResult.setPageNum((int) (long) rolePage.getCurrent());
            pageResult.setPageSize((int) (long) rolePage.getSize());
            pageResult.setTotalPages((int) (long) rolePage.getPages());

            return CommonResult.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询角色失败", e);
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "分页查询角色失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<List<RoleVO>> getAllRoles() {
        try {
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysRole::getStatus, 0)
                    .eq(SysRole::getDeleted, false)
                    .orderByAsc(SysRole::getCreateTime);

            List<SysRole> roles = roleMapper.selectList(queryWrapper);
            List<RoleVO> roleVOs = roles.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());

            return CommonResult.success(roleVOs);
        } catch (Exception e) {
            log.error("获取所有角色失败", e);
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "获取所有角色失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> createRole(RoleCreateDTO createDTO) {
        try {
            SysRole role = new SysRole();
            BeanUtils.copyProperties(createDTO, role);
            role.setCreator("system");
            role.setCreateTime(LocalDateTime.now());

            int result = roleMapper.insert(role);
            if (result <= 0) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "创建角色失败");
            }

            return CommonResult.success();
        } catch (Exception e) {
            log.error("创建角色失败", e);
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "创建角色失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> updateRole(Long id, RoleUpdateDTO updateDTO) {
        try {
            SysRole role = roleMapper.selectById(id);
            if (role == null) {
                return CommonResult.error(HttpCodeEnum.NOT_FOUND, "角色不存在");
            }

            BeanUtils.copyProperties(updateDTO, role);
            role.setId(id);
            role.setUpdater("system");
            role.setUpdateTime(LocalDateTime.now());

            int result = roleMapper.updateById(role);
            if (result <= 0) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新角色失败");
            }

            return CommonResult.success();
        } catch (Exception e) {
            log.error("更新角色失败", e);
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新角色失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> updateRoleStatus(Long id, Integer status) {
        try {
            SysRole role = roleMapper.selectById(id);
            if (role == null) {
                return CommonResult.error(HttpCodeEnum.NOT_FOUND, "角色不存在");
            }

            role.setId(id);
            role.setStatus(status);
            role.setUpdater("system");
            role.setUpdateTime(LocalDateTime.now());

            int result = roleMapper.updateById(role);
            if (result <= 0) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新角色状态失败");
            }

            return CommonResult.success();
        } catch (Exception e) {
            log.error("更新角色状态失败", e);
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新角色状态失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> deleteRole(Long id) {
        try {
            SysRole role = roleMapper.selectById(id);
            if (role == null) {
                return CommonResult.error(HttpCodeEnum.NOT_FOUND, "角色不存在");
            }

            if ("SUPER_ADMIN".equals(role.getCode())) {
                return CommonResult.error(HttpCodeEnum.NO_OPERATOR_AUTH, "不能删除超级管理员角色");
            }

            int result = roleMapper.deleteById(id);
            if (result <= 0) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "删除角色失败");
            }

            return CommonResult.success();
        } catch (Exception e) {
            log.error("删除角色失败", e);
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "删除角色失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<List<Long>> getRoleMenuIds(Long roleId) {
        try {
            SysRole role = roleMapper.selectById(roleId);
            if (role == null) {
                return CommonResult.error(HttpCodeEnum.NOT_FOUND, "角色不存在");
            }

            LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysRoleMenu::getRoleId, roleId);

            List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper);
            List<Long> menuIds = roleMenus.stream()
                    .map(SysRoleMenu::getMenuId)
                    .collect(Collectors.toList());

            return CommonResult.success(menuIds);
        } catch (Exception e) {
            log.error("获取角色菜单权限失败", e);
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "获取角色菜单权限失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> updateRoleMenus(Long roleId, List<Long> menuIds) {
        try {
            SysRole role = roleMapper.selectById(roleId);
            if (role == null) {
                return CommonResult.error(HttpCodeEnum.NOT_FOUND, "角色不存在");
            }

            if ("SUPER_ADMIN".equals(role.getCode())) {
                return CommonResult.error(HttpCodeEnum.NO_OPERATOR_AUTH, "不能修改超级管理员角色权限");
            }

            LambdaQueryWrapper<SysRoleMenu> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(SysRoleMenu::getRoleId, roleId);
            roleMenuMapper.delete(deleteWrapper);

            if (menuIds != null && !menuIds.isEmpty()) {
                List<SysRoleMenu> roleMenus = new ArrayList<>();
                for (Long menuId : menuIds) {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    roleMenu.setCreator("system");
                    roleMenus.add(roleMenu);
                }

                for (SysRoleMenu roleMenu : roleMenus) {
                    roleMenuMapper.insert(roleMenu);
                }
            }

            return CommonResult.success();
        } catch (Exception e) {
            log.error("更新角色菜单权限失败", e);
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新角色菜单权限失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<List<MenuVO>> getMenuTree() {
        try {
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysMenu::getStatus, 0)
                    .eq(SysMenu::getDeleted, false)
                    .orderByAsc(SysMenu::getOrderNum);

            List<SysMenu> menus = menuMapper.selectList(queryWrapper);
            List<MenuVO> menuVOs = menus.stream()
                    .map(this::convertToMenuVO)
                    .collect(Collectors.toList());

            // 构建树形结构
            List<MenuVO> menuTree = buildMenuTree(menuVOs);

            return CommonResult.success(menuTree);
        } catch (Exception e) {
            log.error("获取菜单树失败", e);
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "获取菜单树失败: " + e.getMessage());
        }
    }

    /**
     * 构建菜单树形结构
     */
    private List<MenuVO> buildMenuTree(List<MenuVO> menuList) {
        List<MenuVO> returnList = new ArrayList<>();
        List<Long> tempList = menuList.stream().map(MenuVO::getId).collect(Collectors.toList());

        for (MenuVO menu : menuList) {
            // 如果是顶级菜单
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                recursionFn(menuList, menu);
                returnList.add(menu);
            }
        }

        if (returnList.isEmpty()) {
            returnList = menuList;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<MenuVO> list, MenuVO t) {
        // 得到子节点列表
        List<MenuVO> childList = getChildList(list, t);
        t.setChildren(childList);
        for (MenuVO tChild : childList) {
            // 判断是否有子节点
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<MenuVO> getChildList(List<MenuVO> list, MenuVO t) {
        List<MenuVO> tlist = new ArrayList<>();
        Iterator<MenuVO> it = list.iterator();
        while (it.hasNext()) {
            MenuVO n = it.next();
            if (n.getParentId() != null && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<MenuVO> list, MenuVO t) {
        return getChildList(list, t).size() > 0;
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return roleMapper.selectById(id) != null;
    }

    @Override
    public boolean existsByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getCode, code)
                .eq(SysRole::getDeleted, false);
        return roleMapper.selectOne(queryWrapper) != null;
    }

    @Override
    public RoleVO getById(Long id) {
        if (id == null) {
            return null;
        }
        SysRole role = roleMapper.selectById(id);
        return role != null ? convertToVO(role) : null;
    }

    @Override
    public boolean hasUsers(Long id) {
        if (id == null) {
            return false;
        }
        try {
            LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUserRole::getRoleId, id);
            Long count = userRoleMapper.selectCount(queryWrapper);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("检查角色用户关联失败", e);
            return false;
        }
    }

    private MenuVO convertToMenuVO(SysMenu menu) {
        MenuVO menuVO = new MenuVO();
        BeanUtils.copyProperties(menu, menuVO);
        return menuVO;
    }

    private RoleVO convertToVO(SysRole role) {
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        return roleVO;
    }
}