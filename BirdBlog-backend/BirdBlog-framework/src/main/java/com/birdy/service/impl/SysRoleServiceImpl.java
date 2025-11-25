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
import java.util.*;
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

            // 注释掉这个限制，允许超级管理员修改任何角色的权限
            // if ("SUPER_ADMIN".equals(role.getCode())) {
            //     return CommonResult.error(HttpCodeEnum.NO_OPERATOR_AUTH, "不能修改超级管理员角色权限");
            // }

            // 使用事务安全的方式：先删除，再插入
            // 删除该角色的所有菜单权限（物理删除，避免唯一键冲突）
            roleMenuMapper.physicalDeleteByRoleId(roleId);

            // 如果没有菜单权限要分配，直接返回
            if (menuIds == null || menuIds.isEmpty()) {
                return CommonResult.success();
            }

            // 构建智能的菜单权限列表
            Set<Long> finalMenuIds = buildSmartMenuPermissions(menuIds);

            // 批量插入新的菜单权限
            for (Long menuId : finalMenuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                // creator和updater会通过自动填充处理
                roleMenuMapper.insert(roleMenu);
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

    /**
     * 构建智能的菜单权限列表
     *
     * 处理逻辑：
     * 1. 如果用户选择了父菜单的所有子菜单，则只保存父菜单（简化权限）
     * 2. 如果用户只选择了部分子菜单，则只保存子菜单（避免回显时父菜单被完全选中）
     * 3. 如果用户只选择了父菜单，则保存父菜单（表示全选）
     *
     * @param selectedMenuIds 用户选择的菜单ID列表
     * @return 处理后的菜单权限ID集合
     */
    private Set<Long> buildSmartMenuPermissions(List<Long> selectedMenuIds) {
        if (selectedMenuIds == null || selectedMenuIds.isEmpty()) {
            return new HashSet<>();
        }

        try {
            // 获取所有菜单信息
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysMenu::getStatus, 0)
                    .eq(SysMenu::getDeleted, false);
            List<SysMenu> allMenus = menuMapper.selectList(queryWrapper);

            // 构建菜单映射（ID -> 菜单实体）
            Map<Long, SysMenu> menuMap = allMenus.stream()
                    .collect(Collectors.toMap(SysMenu::getId, menu -> menu));

            // 构建父子关系映射
            Map<Long, List<Long>> parentToChildrenMap = new HashMap<>();
            Set<Long> allMenuIds = new HashSet<>(selectedMenuIds);

            for (SysMenu menu : allMenus) {
                Long parentId = menu.getParentId();
                if (parentId != null && parentId != 0) {
                    parentToChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(menu.getId());
                    allMenuIds.add(menu.getId());
                }
            }

            Set<Long> result = new HashSet<>();
            Set<Long> processedParents = new HashSet<>();

            // 处理每个可能存在父子关系的菜单
            for (Long parentId : parentToChildrenMap.keySet()) {
                List<Long> children = parentToChildrenMap.get(parentId);

                if (children == null || children.isEmpty()) {
                    continue;
                }

                // 检查用户是否选择了这个父菜单
                boolean parentSelected = selectedMenuIds.contains(parentId);

                // 检查用户选择了哪些子菜单
                List<Long> selectedChildren = children.stream()
                        .filter(selectedMenuIds::contains)
                        .collect(Collectors.toList());

                if (parentSelected) {
                    // 用户选择了父菜单
                    if (selectedChildren.size() == children.size()) {
                        // 选择了父菜单和所有子菜单，只保留父菜单
                        result.add(parentId);
                        processedParents.add(parentId);
                    } else if (selectedChildren.isEmpty()) {
                        // 只选择了父菜单，表示全选
                        result.add(parentId);
                        processedParents.add(parentId);
                    } else {
                        // 选择了父菜单和部分子菜单，只保留子菜单
                        result.addAll(selectedChildren);
                        processedParents.add(parentId);
                    }
                } else {
                    // 用户没有选择父菜单，只保留选择的子菜单
                    result.addAll(selectedChildren);
                    processedParents.add(parentId);
                }
            }

            // 添加没有子菜单的菜单（叶子节点）或不在任何父子关系中的菜单
            for (Long menuId : selectedMenuIds) {
                if (!parentToChildrenMap.containsKey(menuId) && !processedParents.contains(menuId)) {
                    result.add(menuId);
                }
            }

            return result;

        } catch (Exception e) {
            log.error("构建智能菜单权限失败，使用原始选择", e);
            // 如果出错，至少返回去重后的用户选择
            return new HashSet<>(selectedMenuIds);
        }
    }

    /**
     * 递归添加菜单的所有父菜单
     */
    private void addParentMenus(Long menuId, Map<Long, SysMenu> menuMap, Set<Long> parentIds) {
        if (menuId == null || !menuMap.containsKey(menuId)) {
            return;
        }

        SysMenu menu = menuMap.get(menuId);
        Long parentId = menu.getParentId();

        // 如果存在父菜单且父菜单不是根菜单（parent_id != 0）
        if (parentId != null && parentId != 0) {
            if (menuMap.containsKey(parentId)) {
                parentIds.add(parentId);
                // 递归查找上级父菜单
                addParentMenus(parentId, menuMap, parentIds);
            }
        }
    }
}