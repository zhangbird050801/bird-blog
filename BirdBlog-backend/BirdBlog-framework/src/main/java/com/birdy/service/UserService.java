package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.UpdateUserInfoRequest;
import com.birdy.domain.dto.UserQueryDTO;
import com.birdy.domain.dto.UserRoleUpdateDTO;
import com.birdy.domain.entity.User;
import com.birdy.domain.vo.AdminUserVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.domain.vo.UserInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Birdy
* @description 针对表【sys_user(用户表)】的数据库操作Service
* @createDate 2025-10-30 13:06:55
*/
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     */
    CommonResult<PageResult<AdminUserVO>> getUserPage(UserQueryDTO queryDTO);

    /**
     * 更新用户状态
     */
    CommonResult<Void> updateUserStatus(Long userId, Integer status);

    /**
     * 更新用户角色
     */
    CommonResult<Void> updateUserRoles(Long userId, UserRoleUpdateDTO updateDTO);

    /**
     * 获取用户详细信息（包括角色和权限）
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 获取当前用户个人信息
     */
    CommonResult getCurrentUserInfo();

    /**
     * 更新用户个人信息
     */
    CommonResult updateUserInfo(UpdateUserInfoRequest request);

    /**
     * 上传用户头像
     */
    CommonResult uploadAvatar(org.springframework.web.multipart.MultipartFile file);
}
