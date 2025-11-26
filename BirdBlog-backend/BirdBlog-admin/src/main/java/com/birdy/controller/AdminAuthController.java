package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.ChangePasswordRequestDTO;
import com.birdy.domain.dto.LoginRequestDTO;
import com.birdy.domain.vo.CaptchaVO;
import com.birdy.domain.vo.LoginVO;
import com.birdy.enums.LoginScene;
import com.birdy.service.LoginService;
import com.birdy.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台认证 Controller
 *
 * @author Birdy
 * @date 2025/10/31
 * @description 管理后台用户登录认证接口
 */
@RestController
@RequestMapping("/auth")
public class AdminAuthController {

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
     * 管理员登录
     *
     * @param loginRequestDTO 登录请求参数
     * @return 登录结果(Token 和用户信息)
     */
    @PostMapping("/login")
    public CommonResult<LoginVO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return loginService.login(loginRequestDTO, LoginScene.ADMIN);
    }

    /**
     * 管理员退出登录
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
     * 修改密码
     *
     * @param changePasswordRequestDTO 修改密码请求参数
     * @return 修改结果
     */
    @PostMapping("/change-password")
    public CommonResult<String> changePassword(@RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {
        System.out.println("AdminAuthController: 收到修改密码请求");
        return loginService.changePassword(changePasswordRequestDTO);
    }
}
