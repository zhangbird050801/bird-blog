package com.birdy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.birdy.config.JwtProperties;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.LoginRequest;
import com.birdy.domain.entity.User;
import com.birdy.domain.vo.LoginVO;
import com.birdy.domain.vo.UserInfoVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.mapper.UserMapper;
import com.birdy.service.LoginService;
import com.birdy.utils.CaptchaUtil;
import com.birdy.utils.JwtUtil;
import com.birdy.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 登录 Service 实现类
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 用户登录认证服务实现
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CaptchaUtil captchaUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public CommonResult<LoginVO> login(LoginRequest loginRequest) {
        // 1. 校验参数
        if (!StringUtils.hasText(loginRequest.getUserName()) || !StringUtils.hasText(loginRequest.getPassword())) {
            return CommonResult.error(HttpCodeEnum.REQUIRE_USERNAME);
        }

        // 2. 校验验证码(如果提供了验证码)
        if (StringUtils.hasText(loginRequest.getCode()) && StringUtils.hasText(loginRequest.getUuid())) {
            if (!captchaUtil.verifyCaptcha(loginRequest.getUuid(), loginRequest.getCode())) {
                return CommonResult.error(HttpCodeEnum.CAPTCHA_ERROR_OR_EXPIRE);
            }
        }

        // 3. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginRequest.getUserName());
        queryWrapper.eq(User::getDeleted, false);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return CommonResult.error(HttpCodeEnum.LOGIN_ERROR);
        }

        // 4. 校验密码(数据库中是加密后的密码)
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return CommonResult.error(HttpCodeEnum.LOGIN_ERROR);
        }

        // 5. 校验用户状态
        if (user.getStatus() != null && user.getStatus() == 1) {
            return CommonResult.error(HttpCodeEnum.USER_BANNED);
        }

        // 6. 生成 JWT Token
        String userId = user.getId().toString();
        String token = JwtUtil.createJWT(userId);

        // 7. 将用户信息存入 Redis(可选,用于后续验证和获取用户信息)
        redisUtil.set("login:" + userId, JSON.toJSONString(user), jwtProperties.getTtl() / 1000);

        // 8. 封装返回结果
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setNickName(user.getNickName());
        userInfoVO.setAvatar(user.getAvatar());
        userInfoVO.setSex(user.getSex() != null ? user.getSex().toString() : "2");
        userInfoVO.setEmail(user.getEmail());

        LoginVO loginVO = new LoginVO(token, userInfoVO);

        return CommonResult.success(loginVO);
    }

    @Override
    public CommonResult<Void> logout() {
        // TODO: 从 SecurityContextHolder 获取当前用户 ID,删除 Redis 中的用户信息
        // 暂时返回成功
        return CommonResult.success();
    }
}
