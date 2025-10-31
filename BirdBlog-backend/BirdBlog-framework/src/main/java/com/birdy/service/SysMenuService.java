package com.birdy.service;

import com.birdy.domain.dto.MenuFormDTO;
import com.birdy.domain.entity.SysMenu;
import com.birdy.domain.vo.MenuVO;

import java.util.List;
import java.util.Map;

/**
 * 菜单权限 Service 接口
 *
 * @author Birdy
 * @date 2025/10/31
 * @description 系统菜单权限业务逻辑接口
 */
public interface SysMenuService {

    /**
     * 获取当前用户的路由菜单列表
     * （前端用于动态生成菜单和路由）
     *
     * @return 路由列表
     */
    List<Map<String, Object>> getRoutes();

    /**
     * 获取菜单树形列表
     *
     * @param name 菜单名称（模糊查询）
     * @param status 状态（0正常 1停用）
     * @return 菜单树形列表
     */
    List<MenuVO> getMenuTree(String name, Integer status);

    /**
     * 获取菜单下拉选项（用于选择父菜单）
     *
     * @return 菜单树形选项
     */
    List<Map<String, Object>> getMenuOptions();

    /**
     * 根据ID获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    MenuVO getMenuById(Long id);

    /**
     * 新增菜单
     *
     * @param menuForm 菜单表单
     * @return 是否成功
     */
    boolean addMenu(MenuFormDTO menuForm);

    /**
     * 修改菜单
     *
     * @param id 菜单ID
     * @param menuForm 菜单表单
     * @return 是否成功
     */
    boolean updateMenu(Long id, MenuFormDTO menuForm);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否成功
     */
    boolean deleteMenu(Long id);
}
