package com.birdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.birdy.domain.dto.MenuFormDTO;
import com.birdy.domain.entity.SysMenu;
import com.birdy.domain.vo.MenuVO;
import com.birdy.mapper.SysMenuMapper;
import com.birdy.service.SysMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public List<Map<String, Object>> getRoutes() {
        // 1. 查询所有正常状态且可见的菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getStatus, 0)      // 状态：0正常
                   .eq(SysMenu::getVisible, 0)      // 显示：0显示
                   .eq(SysMenu::getDeleted, false)  // 未删除
                   .orderByAsc(SysMenu::getOrderNum, SysMenu::getId);
        
        List<SysMenu> allMenus = sysMenuMapper.selectList(queryWrapper);
        
        // 2. 构建树形结构
        return buildMenuTree(allMenus, null);
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
}
