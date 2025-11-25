package com.birdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.UserQueryDTO;
import com.birdy.domain.dto.UserRoleUpdateDTO;
import com.birdy.domain.entity.User;
import com.birdy.domain.entity.SysRole;
import com.birdy.domain.entity.SysUserRole;
import com.birdy.domain.vo.AdminUserVO;
import com.birdy.domain.vo.RoleVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.domain.vo.UserInfoVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.mapper.UserMapper;
import com.birdy.mapper.SysRoleMapper;
import com.birdy.mapper.SysUserRoleMapper;
import com.birdy.mapper.SysRoleMenuMapper;
import com.birdy.mapper.SysMenuMapper;
import com.birdy.domain.entity.SysRoleMenu;
import com.birdy.domain.entity.SysMenu;
import com.birdy.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public CommonResult<PageResult<AdminUserVO>> getUserPage(UserQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new UserQueryDTO();
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDeleted, false);

        String keyword = queryDTO.getKeyword();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(User::getUsername, keyword)
                .or().like(User::getNickName, keyword)
                .or().like(User::getEmail, keyword)
                .or().like(User::getPhone, keyword)
            );
        }

        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(User::getStatus, queryDTO.getStatus());
        }

        // queryWrapper.orderByAsc(User::getId()); // 简化实现，暂时不排序

        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        int pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10;

        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> resultPage = baseMapper.selectPage(page, queryWrapper);

        List<AdminUserVO> rows = resultPage.getRecords().stream()
            .map(this::convertToAdminUserVO)
            .collect(Collectors.toList());

        PageResult<AdminUserVO> pageResult = new PageResult<>(
            resultPage.getTotal(),
            rows,
            (int) resultPage.getCurrent(),
            (int) resultPage.getSize()
        );

        return CommonResult.success(pageResult);
    }

    @Override
    public CommonResult<Void> updateUserStatus(Long userId, Integer status) {
        if (userId == null || status == null ||
            (status != SysConstants.USER_STATUS_NORMAL && status != SysConstants.USER_STATUS_DISABLE)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "参数非法");
        }

        User user = baseMapper.selectById(userId);
        if (user == null || Boolean.TRUE.equals(user.getDeleted())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "用户不存在");
        }

        if (user.getStatus() != null && user.getStatus().equals(status)) {
            return CommonResult.success();
        }

        user.setStatus(status);
        user.setUpdateTime(new Date());
        int rows = baseMapper.updateById(user);
        if (rows > 0) {
            return CommonResult.success();
        }
        return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新用户状态失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> updateUserRoles(Long userId, UserRoleUpdateDTO updateDTO) {
        if (userId == null || updateDTO == null || updateDTO.getRoleIds() == null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "参数非法");
        }

        User user = baseMapper.selectById(userId);
        if (user == null || Boolean.TRUE.equals(user.getDeleted())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "用户不存在");
        }

        try {
            LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(SysUserRole::getUserId, userId);
            userRoleMapper.delete(deleteWrapper);

            List<SysUserRole> userRoles = new ArrayList<>();
            for (Long roleId : updateDTO.getRoleIds()) {
                SysRole role = roleMapper.selectById(roleId);
                if (role != null && role.getStatus() == 0) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRole.setCreator("system");
                    userRoles.add(userRole);
                }
            }

            for (SysUserRole userRole : userRoles) {
                userRoleMapper.insert(userRole);
            }

            return CommonResult.success();
        } catch (Exception e) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新用户角色失败: " + e.getMessage());
        }
    }

    private AdminUserVO convertToAdminUserVO(User user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickName(user.getNickName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setSex(user.getSex());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        try {
            LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(SysUserRole::getUserId, user.getId());

            List<SysUserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
            if (!userRoles.isEmpty()) {
                List<Long> roleIds = userRoles.stream()
                        .map(SysUserRole::getRoleId)
                        .collect(Collectors.toList());

                if (!roleIds.isEmpty()) {
                    LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
                    roleWrapper.in(SysRole::getId, roleIds)
                            .eq(SysRole::getStatus, 0);
                    roleWrapper.orderByAsc(SysRole::getCreateTime);

                    List<SysRole> roles = roleMapper.selectList(roleWrapper);
                    List<RoleVO> roleVOs = roles.stream()
                            .map(this::convertToRoleVO)
                            .collect(Collectors.toList());

                    vo.setRoles(roleVOs);
                }
            }
        } catch (Exception e) {
            // 角色信息获取失败不影响用户基本信息的返回
        }

        return vo;
    }

    private RoleVO convertToRoleVO(SysRole role) {
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        return roleVO;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        try {
            // 1. 获取用户基本信息
            User user = this.getById(userId);
            if (user == null) {
                return null;
            }

            // 2. 转换为UserInfoVO基本信息
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setId(user.getId());
            userInfoVO.setNickName(user.getNickName());
            userInfoVO.setAvatar(user.getAvatar());
            userInfoVO.setSex(user.getSex() != null ? user.getSex().toString() : "2");
            userInfoVO.setEmail(user.getEmail());
            // 移除type字段设置，通过角色来管理用户权限

            // 3. 获取用户角色信息
            List<RoleVO> roles = getUserRoles(userId);
            userInfoVO.setRoles(roles);

            // 4. 获取用户权限信息
            List<String> permissions = getUserPermissions(userId);
            userInfoVO.setPermissions(permissions);

            return userInfoVO;
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户角色列表
     */
    private List<RoleVO> getUserRoles(Long userId) {
        try {
            LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(SysUserRole::getUserId, userId);

            List<SysUserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
            if (userRoles.isEmpty()) {
                return new ArrayList<>();
            }

            List<Long> roleIds = userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .collect(Collectors.toList());

            if (roleIds.isEmpty()) {
                return new ArrayList<>();
            }

            LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(SysRole::getId, roleIds)
                    .eq(SysRole::getStatus, 0);
            roleWrapper.orderByAsc(SysRole::getCreateTime);

            List<SysRole> roles = roleMapper.selectList(roleWrapper);
            return roles.stream()
                    .map(this::convertToRoleVO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // 角色信息获取失败时返回空列表，不影响其他信息
            return new ArrayList<>();
        }
    }

    /**
     * 获取用户权限列表
     */
    private List<String> getUserPermissions(Long userId) {
        try {
            // 获取用户的角色ID列表
            LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(SysUserRole::getUserId, userId)
                    .eq(SysUserRole::getDeleted, false);

            List<SysUserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
            if (userRoles.isEmpty()) {
                return new ArrayList<>();
            }

            List<Long> roleIds = userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .collect(Collectors.toList());

            // 获取角色对应的菜单权限
            LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
            roleMenuWrapper.in(SysRoleMenu::getRoleId, roleIds)
                    .eq(SysRoleMenu::getDeleted, false);

            List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(roleMenuWrapper);
            if (roleMenus.isEmpty()) {
                return new ArrayList<>();
            }

            List<Long> menuIds = roleMenus.stream()
                    .map(SysRoleMenu::getMenuId)
                    .distinct()
                    .collect(Collectors.toList());

            // 获取菜单的权限标识
            LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<>();
            menuWrapper.in(SysMenu::getId, menuIds)
                    .isNotNull(SysMenu::getPerms)
                    .ne(SysMenu::getPerms, "")
                    .eq(SysMenu::getDeleted, false)
                    .eq(SysMenu::getStatus, 0);

            List<SysMenu> menus = sysMenuMapper.selectList(menuWrapper);

            // 提取权限标识
            List<String> permissions = menus.stream()
                    .map(SysMenu::getPerms)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            // 超级管理员添加所有权限
            boolean isSuperAdmin = userRoles.stream()
                    .anyMatch(ur -> {
                        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
                        roleWrapper.eq(SysRole::getId, ur.getRoleId())
                                .eq(SysRole::getCode, "SUPER_ADMIN")
                                .eq(SysRole::getDeleted, false);
                        return roleMapper.selectCount(roleWrapper) > 0;
                    });

            if (isSuperAdmin) {
                permissions.add("*:*");
            }

            return permissions;
        } catch (Exception e) {
            // 权限信息获取失败时返回空列表，不影响其他信息
            return new ArrayList<>();
        }
    }
}