package com.birdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.birdy.domain.dto.MenuFormDTO;
import com.birdy.domain.entity.SysMenu;
import com.birdy.domain.entity.SysRole;
import com.birdy.domain.entity.SysRoleMenu;
import com.birdy.domain.entity.SysUserRole;
import com.birdy.domain.vo.MenuVO;
import com.birdy.mapper.SysMenuMapper;
import com.birdy.mapper.SysRoleMenuMapper;
import com.birdy.mapper.SysUserRoleMapper;
import com.birdy.service.SysMenuService;
import com.birdy.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单权限 Service 实现类
 *
 * @author Birdy
 * @date 2025/10/31
 * @description 系统菜单权限业务逻辑实现
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<Map<String, Object>> getRoutes() {
        // 1. 获取当前用户信息
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return new ArrayList<>();
        }

        // 2. 获取当前用户的角色ID列表
        List<Long> userRoleIds = getUserRoleIds(userId);
        if (userRoleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 根据角色获取菜单权限
        List<Long> menuIds = getMenuIdsByRoles(userRoleIds);
        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 查询用户有权限的菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysMenu::getId, menuIds)     // 只查询有权限的菜单
                   .eq(SysMenu::getStatus, 0)          // 状态：0正常
                   .eq(SysMenu::getVisible, 0)          // 显示：0显示
                   .eq(SysMenu::getDeleted, false)      // 未删除
                   .orderByAsc(SysMenu::getOrderNum, SysMenu::getId);

        List<SysMenu> userMenus = sysMenuMapper.selectList(queryWrapper);

        // 5. 构建树形结构
        return buildMenuTree(userMenus, null);
    }

    /**
     * 构建菜单树
     *
     * @param allMenus 所有菜单列表
     * @param parentId 父菜单ID
     * @return 树形菜单列表
     */
    private List<Map<String, Object>> buildMenuTree(List<SysMenu> allMenus, Long parentId) {
        List<Map<String, Object>> routes = new ArrayList<>();
        
        // 过滤出当前层级的菜单
        List<SysMenu> currentLevelMenus = allMenus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .collect(Collectors.toList());
        
        for (SysMenu menu : currentLevelMenus) {
            Map<String, Object> route = new HashMap<>();
            
            // 基本字段
            route.put("path", menu.getPath());
            route.put("component", menu.getComponent());
            route.put("name", convertToRouteName(menu.getPath())); // 根据path生成name
            
            // meta 信息
            Map<String, Object> meta = new HashMap<>();
            meta.put("title", menu.getMenuName());
            meta.put("icon", menu.getIcon());
            meta.put("hidden", menu.getVisible() == 1);
            meta.put("alwaysShow", "M".equals(menu.getMenuType())); // 目录始终显示
            
            // 如果是菜单，添加权限标识
            if ("C".equals(menu.getMenuType()) && menu.getPerms() != null) {
                meta.put("roles", Arrays.asList(menu.getPerms()));
            }
            
            route.put("meta", meta);
            
            // 递归查找子菜单
            List<Map<String, Object>> children = buildMenuTree(allMenus, menu.getId());
            if (!children.isEmpty()) {
                route.put("children", children);
            }
            
            // 如果有重定向（目录类型且有子菜单）
            if ("M".equals(menu.getMenuType()) && !children.isEmpty()) {
                String firstChildPath = (String) ((Map<String, Object>) children.get(0)).get("path");
                // 构建正确的 redirect 路径
                String redirectPath;
                if (menu.getPath().startsWith("/")) {
                    redirectPath = menu.getPath() + "/" + firstChildPath;
                } else {
                    redirectPath = "/" + menu.getPath() + "/" + firstChildPath;
                }
                route.put("redirect", redirectPath);
            }
            
            routes.add(route);
        }
        
        return routes;
    }

    /**
     * 将路径转换为路由名称
     * 例如：/dashboard -> Dashboard, /blog/article -> BlogArticle
     *
     * @param path 路径
     * @return 路由名称
     */
    private String convertToRouteName(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        
        // 移除开头的 /
        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        
        // 按 / 分割并首字母大写
        String[] parts = cleanPath.split("/");
        StringBuilder name = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                name.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1));
            }
        }
        
        return name.toString();
    }

    @Override
    public List<MenuVO> getMenuTree(String name, Integer status) {
        // 1. 查询所有菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getDeleted, false)
                   .orderByAsc(SysMenu::getOrderNum, SysMenu::getId);
        
        // 添加过滤条件
        if (StringUtils.hasText(name)) {
            queryWrapper.like(SysMenu::getMenuName, name);
        }
        if (status != null) {
            queryWrapper.eq(SysMenu::getStatus, status);
        }
        
        List<SysMenu> allMenus = sysMenuMapper.selectList(queryWrapper);
        
        // 2. 转换为 VO 并构建树形结构
        List<MenuVO> menuVOList = allMenus.stream()
                .map(this::convertToMenuVO)
                .collect(Collectors.toList());
        
        return buildMenuVOTree(menuVOList, null);
    }

    @Override
    public List<Map<String, Object>> getMenuOptions() {
        // 查询所有目录和菜单类型的菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getDeleted, false)
                   .in(SysMenu::getMenuType, Arrays.asList("M", "C"))
                   .orderByAsc(SysMenu::getOrderNum, SysMenu::getId);
        
        List<SysMenu> allMenus = sysMenuMapper.selectList(queryWrapper);
        
        return buildMenuOptions(allMenus, null);
    }

    @Override
    public MenuVO getMenuById(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null || menu.getDeleted()) {
            return null;
        }
        return convertToMenuVO(menu);
    }

    @Override
    public boolean addMenu(MenuFormDTO menuForm) {
        SysMenu menu = new SysMenu();
        
        // 手动映射字段（DTO字段名与实体字段名不同）
        menu.setMenuName(menuForm.getName());
        menu.setParentId(menuForm.getParentId());
        menu.setOrderNum(menuForm.getSort());
        menu.setPath(menuForm.getPath());
        menu.setComponent(menuForm.getComponent());
        menu.setMenuType(menuForm.getType());
        menu.setVisible(menuForm.getVisible());
        menu.setStatus(menuForm.getStatus());
        menu.setPerms(menuForm.getPerm());
        menu.setIcon(menuForm.getIcon());
        menu.setRemark(menuForm.getRemark());
        
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        menu.setDeleted(false);
        menu.setIsFrame(1); // 默认不是外链
        
        return sysMenuMapper.insert(menu) > 0;
    }

    @Override
    public boolean updateMenu(Long id, MenuFormDTO menuForm) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null || menu.getDeleted()) {
            return false;
        }
        
        // 手动映射字段（DTO字段名与实体字段名不同）
        menu.setMenuName(menuForm.getName());
        menu.setParentId(menuForm.getParentId());
        menu.setOrderNum(menuForm.getSort());
        menu.setPath(menuForm.getPath());
        menu.setComponent(menuForm.getComponent());
        menu.setMenuType(menuForm.getType());
        menu.setVisible(menuForm.getVisible());
        menu.setStatus(menuForm.getStatus());
        menu.setPerms(menuForm.getPerm());
        menu.setIcon(menuForm.getIcon());
        menu.setRemark(menuForm.getRemark());
        
        menu.setUpdateTime(LocalDateTime.now());
        
        return sysMenuMapper.updateById(menu) > 0;
    }

    @Override
    public boolean deleteMenu(Long id) {
        // 检查是否存在子菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId, id)
                   .eq(SysMenu::getDeleted, false);
        
        Long count = sysMenuMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("存在子菜单，无法删除");
        }
        
        // 逻辑删除
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            return false;
        }
        
        menu.setDeleted(true);
        menu.setUpdateTime(LocalDateTime.now());
        
        return sysMenuMapper.updateById(menu) > 0;
    }

    /**
     * 将 SysMenu 转换为 MenuVO
     *
     * @param menu 菜单实体
     * @return 菜单 VO
     */
    private MenuVO convertToMenuVO(SysMenu menu) {
        MenuVO menuVO = new MenuVO();
        BeanUtils.copyProperties(menu, menuVO);
        return menuVO;
    }

    /**
     * 构建菜单 VO 树形结构
     *
     * @param allMenus 所有菜单 VO 列表
     * @param parentId 父菜单 ID
     * @return 树形菜单 VO 列表
     */
    private List<MenuVO> buildMenuVOTree(List<MenuVO> allMenus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();
        
        for (MenuVO menu : allMenus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                List<MenuVO> children = buildMenuVOTree(allMenus, menu.getId());
                menu.setChildren(children);
                tree.add(menu);
            }
        }
        
        return tree;
    }

    /**
     * 构建菜单选项树
     *
     * @param allMenus 所有菜单列表
     * @param parentId 父菜单 ID
     * @return 树形选项列表
     */
    private List<Map<String, Object>> buildMenuOptions(List<SysMenu> allMenus, Long parentId) {
        List<Map<String, Object>> options = new ArrayList<>();
        
        List<SysMenu> currentLevelMenus = allMenus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .collect(Collectors.toList());
        
        for (SysMenu menu : currentLevelMenus) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", menu.getId());
            option.put("label", menu.getMenuName());
            
            List<Map<String, Object>> children = buildMenuOptions(allMenus, menu.getId());
            if (!children.isEmpty()) {
                option.put("children", children);
            }
            
            options.add(option);
        }
        
        return options;
    }

    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    private List<Long> getUserRoleIds(Long userId) {
        try {
            LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(SysUserRole::getUserId, userId);

            List<SysUserRole> userRoles = sysUserRoleMapper.selectList(userRoleWrapper);
            return userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据角色ID列表获取菜单权限ID列表
     *
     * @param roleIds 角色ID列表
     * @return 菜单ID列表
     */
    private List<Long> getMenuIdsByRoles(List<Long> roleIds) {
        try {
            LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
            roleMenuWrapper.in(SysRoleMenu::getRoleId, roleIds);

            List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(roleMenuWrapper);
            return roleMenus.stream()
                    .map(SysRoleMenu::getMenuId)
                    .distinct()  // 去重
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
