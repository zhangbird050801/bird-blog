package com.birdy.interceptor;

import com.alibaba.fastjson.JSON;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.vo.UserInfoVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 后台接口访问拦截器，仅允许管理员访问
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
        if (!Objects.equals(userInfo.getType(), SysConstants.USER_TYPE_AUTHOR)) {
            writeError(response, HttpCodeEnum.NO_OPERATOR_AUTH, "仅管理员可访问后台接口");
            return false;
        }
        return true;
    }

    private void writeError(HttpServletResponse response, HttpCodeEnum codeEnum, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        CommonResult<Object> result = CommonResult.error(codeEnum, message);
        response.getOutputStream().write(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
    }
}
