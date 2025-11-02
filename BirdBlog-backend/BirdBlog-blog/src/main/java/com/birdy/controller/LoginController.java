package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.LoginRequestDTO;
import com.birdy.domain.dto.RegisterRequestDTO;
import com.birdy.domain.vo.CaptchaVO;
import com.birdy.domain.vo.LoginVO;
import com.birdy.domain.vo.RegisterVO;
import com.birdy.service.LoginService;
import com.birdy.utils.CaptchaUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录 Controller
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 用户登录认证接口
 */
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private CaptchaUtil captchaUtil;

    /**
     * 获取验证码
     *
     * @return 验证码图片 Base64 编码和 UUID
     */
    @GetMapping("/captcha")
    public CommonResult<CaptchaVO> getCaptcha() {
        CaptchaUtil.CaptchaResult result = captchaUtil.generateCaptcha();
        CaptchaVO captchaVO = new CaptchaVO(result.getImageBase64(), result.getUuid());
        return CommonResult.success(captchaVO);
    }

    /**
     * 用户登录
     *
     * @param loginRequestDTO 登录请求参数
     * @return 登录结果(Token 和用户信息)
     */
    @PostMapping("/login")
    public CommonResult<LoginVO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return loginService.login(loginRequestDTO);
    }

    /**
     * 用户退出登录
     *
     * @return 退出结果
     */
    @PostMapping("/logout")
    public CommonResult<Void> logout() {
        return loginService.logout();
    }

    /**
     * 刷新 Token
     * 当 AccessToken 过期时，使用 RefreshToken 获取新的令牌对
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌和刷新令牌
     */
    @PostMapping("/refresh-token")
    public CommonResult<LoginVO> refreshToken(@RequestParam String refreshToken) {
        return loginService.refreshToken(refreshToken);
    }

    /**
     * 注册用户
     * 
     * @param registerRequestDTO 注册请求参数（包含用户名、邮箱、密码、验证码）
     * @return 注册结果
     */
    @PostMapping("/register")
    public CommonResult<RegisterVO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return loginService.register(registerRequestDTO);
    }
}
