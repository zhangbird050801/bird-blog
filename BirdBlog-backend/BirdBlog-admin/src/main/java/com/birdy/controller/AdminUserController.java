package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.vo.UserInfoVO;
import com.birdy.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

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
     * 获取用户列表（分页）
     * 暂未实现，返回未实现提示
     */
    @GetMapping("/page")
    public CommonResult<?> getPage() {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "用户管理功能暂未实现");
    }

    /**
     * 获取用户表单详情
     * 暂未实现
     */
    @GetMapping("/{id}/form")
    public CommonResult<?> getFormData(@PathVariable Long id) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "用户管理功能暂未实现");
    }

    /**
     * 添加用户
     * 暂未实现
     */
    @PostMapping
    public CommonResult<?> create(@RequestBody Object data) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "用户管理功能暂未实现");
    }

    /**
     * 修改用户
     * 暂未实现
     */
    @PutMapping("/{id}")
    public CommonResult<?> update(@PathVariable Long id, @RequestBody Object data) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "用户管理功能暂未实现");
    }

    /**
     * 删除用户
     * 暂未实现
     */
    @DeleteMapping("/{ids}")
    public CommonResult<?> delete(@PathVariable String ids) {
        return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "用户管理功能暂未实现");
    }
}
