package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.LoginRequest;
import com.birdy.domain.vo.CaptchaVO;
import com.birdy.domain.vo.LoginVO;
import com.birdy.service.LoginService;
import com.birdy.utils.CaptchaUtil;
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
     * @param loginRequest 登录请求参数
     * @return 登录结果(Token 和用户信息)
     */
    @PostMapping("/login")
    public CommonResult<LoginVO> login(@RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest);
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
}
