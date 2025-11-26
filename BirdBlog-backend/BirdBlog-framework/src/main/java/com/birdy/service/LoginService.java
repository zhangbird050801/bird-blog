package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.ChangePasswordRequestDTO;
import com.birdy.domain.dto.LoginRequestDTO;
import com.birdy.domain.dto.RegisterRequestDTO;
import com.birdy.domain.vo.LoginVO;
import com.birdy.domain.vo.RegisterVO;
import com.birdy.enums.LoginScene;

/**
 * 登录 Service 接口
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 用户登录认证服务
 */
public interface LoginService {

    /**
     * 用户登录
     *
     * @param loginRequestDTO 登录请求参数
     * @return 登录结果(包含 Token 和用户信息)
     */
    CommonResult<LoginVO> login(LoginRequestDTO loginRequestDTO, LoginScene loginScene);

    default CommonResult<LoginVO> login(LoginRequestDTO loginRequestDTO) {
        return login(loginRequestDTO, LoginScene.FRONT);
    }

    /**
     * 用户退出登录
     *
     * @return 退出结果
     */
    CommonResult<Void> logout();

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌和刷新令牌
     */
    CommonResult<LoginVO> refreshToken(String refreshToken);

    /**
     * 注册
     * @param registerRequestDTO
     * @return
     */
    CommonResult<RegisterVO> register(RegisterRequestDTO registerRequestDTO);

    /**
     * 修改密码
     *
     * @param changePasswordRequestDTO 修改密码请求参数
     * @return 修改结果
     */
    CommonResult<String> changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);
}
