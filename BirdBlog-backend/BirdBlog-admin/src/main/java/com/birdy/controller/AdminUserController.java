package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.UserQueryDTO;
import com.birdy.domain.dto.UserStatusUpdateDTO;
import com.birdy.domain.vo.AdminUserVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.domain.vo.UserInfoVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.service.UserService;
import com.birdy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 用户管理 Controller
 *
 * @author Birdy
 * @date 2025/10/31
 * @description 用户信息查询和管理接口
 */
@RestController
@RequestMapping("/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息
     * 前端登录成功后会调用此接口获取用户详细信息、角色、权限等
     *
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public CommonResult<UserInfoVO> getCurrentUserInfo() {
        try {
            // 从 JWT Token 中解析并获取当前用户信息
            UserInfoVO userInfo = SecurityUtils.getUserInfo();
            
            if (userInfo != null) {
                return CommonResult.success(userInfo);
            } else {
                // 未登录或 Token 无效
                return CommonResult.error(
                    com.birdy.enums.HttpCodeEnum.NEED_LOGIN,
                    "未登录或登录已过期"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(
                com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                "获取用户信息失败: " + e.getMessage()
            );
        }
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public CommonResult<PageResult<AdminUserVO>> getPage(UserQueryDTO queryDTO) {
        return userService.getUserPage(queryDTO);
    }

    /**
     * 更新用户状态（启用/停用）
     */
    @PutMapping("/{id}/status")
    public CommonResult<Void> updateStatus(@PathVariable Long id, @RequestBody UserStatusUpdateDTO statusDTO) {
        if (statusDTO == null || statusDTO.getStatus() == null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "状态不能为空");
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null && Objects.equals(currentUserId, id)) {
            return CommonResult.error(HttpCodeEnum.NO_OPERATOR_AUTH, "不能修改自己的状态");
        }

        return userService.updateUserStatus(id, statusDTO.getStatus());
    }
}
