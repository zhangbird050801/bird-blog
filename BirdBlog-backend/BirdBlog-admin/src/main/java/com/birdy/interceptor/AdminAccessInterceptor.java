package com.birdy.interceptor;

import com.alibaba.fastjson.JSON;
import com.birdy.domain.CommonResult;
import com.birdy.domain.vo.UserInfoVO;
import com.birdy.domain.vo.RoleVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 后台接口访问拦截器，仅允许有管理员权限的角色访问
 * 超级管理员、编辑、审核员可以访问后台接口
 * 访客不能访问后台接口
 */
@Component
public class AdminAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoVO userInfo = SecurityUtils.getUserInfo();
        if (userInfo == null) {
            writeError(response, HttpCodeEnum.NEED_LOGIN, "请先登录后台账号");
            return false;
        }

        // 检查用户是否有管理员权限
        if (!hasAdminAccess(userInfo)) {
            writeError(response, HttpCodeEnum.NO_OPERATOR_AUTH, "访客用户不能访问后台接口");
            return false;
        }

        return true;
    }

    /**
     * 检查用户是否有管理员权限
     * 超级管理员、编辑、审核员可以访问后台接口
     * 访客不能访问后台接口
     */
    private boolean hasAdminAccess(UserInfoVO userInfo) {
        List<RoleVO> roles = userInfo.getRoles();
        if (roles == null || roles.isEmpty()) {
            // 用户没有任何角色
            return false;
        }

        // 检查是否有管理员角色（排除访客角色）
        boolean hasAdminRole = roles.stream().anyMatch(role -> {
            String roleCode = role.getCode();
            boolean isAdminRole = "SUPER_ADMIN".equals(roleCode)
                || "EDITOR".equals(roleCode)
                || "REVIEWER".equals(roleCode);

            if (isAdminRole) {
                System.out.println("用户" + userInfo.getNickName() + "拥有管理员角色: " + roleCode);
            }

            return isAdminRole;
        });

        // 如果只有访客角色，拒绝访问
        boolean onlyVisitorRole = roles.stream().allMatch(role -> "VISITOR".equals(role.getCode()));
        if (onlyVisitorRole) {
            // 用户仅有访客角色，不能访问后台
            return false;
        }

        if (!hasAdminRole) {
            // 用户角色不是管理员角色
        }

        return hasAdminRole;
    }

    private void writeError(HttpServletResponse response, HttpCodeEnum codeEnum, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        CommonResult<Object> result = CommonResult.error(codeEnum, message);
        response.getOutputStream().write(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
    }
}
