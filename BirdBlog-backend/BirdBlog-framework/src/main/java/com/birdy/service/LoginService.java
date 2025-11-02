package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.LoginRequestDTO;
import com.birdy.domain.dto.RegisterRequestDTO;
import com.birdy.domain.vo.LoginVO;
import com.birdy.domain.vo.RegisterVO;

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
    CommonResult<LoginVO> login(LoginRequestDTO loginRequestDTO);

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
}
