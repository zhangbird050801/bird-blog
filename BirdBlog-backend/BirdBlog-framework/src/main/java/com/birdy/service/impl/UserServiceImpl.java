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

import com.birdy.utils.RedisUtil;
import com.alibaba.fastjson.JSON;

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

    @Autowired
    private com.birdy.mapper.ArticleFavoriteMapper articleFavoriteMapper;

    @Autowired
    private com.birdy.mapper.ArticleLikeMapper articleLikeMapper;

    @Autowired
    private RedisUtil redisUtil;

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

    @Override
    public CommonResult getCurrentUserInfo() {
        try {
            Long userId = com.birdy.utils.SecurityUtils.getUserId();
            User user = getById(userId);
            if (user == null) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "用户不存在");
            }

            // 获取收藏和点赞数量
            Long favoriteCount = getFavoriteCount(userId);
            Long likeCount = getLikeCount(userId);

            com.birdy.domain.vo.UserProfileVO userProfile = new com.birdy.domain.vo.UserProfileVO();
            userProfile.setId(user.getId());
            userProfile.setUsername(user.getUsername());
            userProfile.setNickName(user.getNickName());
            userProfile.setEmail(user.getEmail());
            userProfile.setPhone(user.getPhone());
            userProfile.setSex(user.getSex());
            userProfile.setAvatar(user.getAvatar());
            userProfile.setCreateTime(user.getCreateTime());
            userProfile.setFavoriteCount(favoriteCount);
            userProfile.setLikeCount(likeCount);

            return CommonResult.success(userProfile);
        } catch (Exception e) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "获取用户信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult updateUserInfo(com.birdy.domain.dto.UpdateUserInfoRequest request) {
        try {
            Long userId = com.birdy.utils.SecurityUtils.getUserId();
            User user = getById(userId);
            if (user == null) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "用户不存在");
            }

            // 更新用户信息
            if (StringUtils.hasText(request.getNickName())) {
                user.setNickName(request.getNickName());
            }
            if (StringUtils.hasText(request.getEmail())) {
                // 检查邮箱是否已被其他用户使用
                LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
                emailWrapper.eq(User::getEmail, request.getEmail())
                           .ne(User::getId, userId)
                           .eq(User::getDeleted, false);
                if (count(emailWrapper) > 0) {
                    return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "邮箱已被使用");
                }
                user.setEmail(request.getEmail());
            }
            if (StringUtils.hasText(request.getPhone())) {
                user.setPhone(request.getPhone());
            }
            if (request.getSex() != null) {
                user.setSex(request.getSex());
            }
            if (StringUtils.hasText(request.getAvatar())) {
                user.setAvatar(request.getAvatar());
            }

            user.setUpdateTime(new Date());
            updateById(user);
            // 同步刷新登录缓存，确保头像等信息即时生效
            refreshLoginCache(user);

            return CommonResult.success("更新成功");
        } catch (Exception e) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新用户信息失败");
        }
    }

    @Autowired
    private com.birdy.utils.CosUtils cosUtils;

    @Override
    public CommonResult uploadAvatar(org.springframework.web.multipart.MultipartFile file) {
        try {
            // 验证文件类型
            String extension = com.birdy.utils.FileUploadUtils.getExtension(file);
            if (!com.birdy.utils.FileUploadUtils.isValidExtension(extension)) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "不支持的文件类型，仅支持图片格式");
            }

            // 上传到腾讯云COS
            String key = cosUtils.uploadImgToCos(file, "avatar");
            
            // 生成带签名的访问URL（私有读写需要签名）
            String avatarUrl = cosUtils.getSignedFileUrl(key);
            
            // 更新用户头像
            Long userId = com.birdy.utils.SecurityUtils.getUserId();
            User user = getById(userId);
            if (user == null) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "用户不存在");
            }

            user.setAvatar(avatarUrl);
            user.setUpdateTime(new Date());
            updateById(user);
            // 同步刷新登录缓存，确保头像在已登录会话中即时更新
            refreshLoginCache(user);

            return CommonResult.success(avatarUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 刷新登录态缓存，保证头像/昵称等信息在已登录会话中即时更新
     */
    private void refreshLoginCache(User user) {
        if (user == null || user.getId() == null || redisUtil == null) {
            return;
        }
        String loginKey = "login:" + user.getId();
        long ttl = redisUtil.getExpire(loginKey);
        String serialized = JSON.toJSONString(user);
        if (ttl > 0) {
            redisUtil.set(loginKey, serialized, ttl);
        } else {
            // 未找到原 TTL 时直接写入，交由默认过期策略
            redisUtil.set(loginKey, serialized);
        }
    }

    /**
     * 获取用户有效的收藏数量（只统计未删除且已发布的文章）
     */
    private Long getFavoriteCount(Long userId) {
        try {
            return articleFavoriteMapper.countValidFavoritesByUserId(userId);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 获取用户有效的点赞数量（只统计未删除且已发布的文章）
     */
    private Long getLikeCount(Long userId) {
        try {
            return articleLikeMapper.countValidLikesByUserId(userId);
        } catch (Exception e) {
            return 0L;
        }
    }
}
