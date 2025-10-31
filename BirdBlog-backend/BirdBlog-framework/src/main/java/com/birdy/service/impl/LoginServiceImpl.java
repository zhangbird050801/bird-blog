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
import io.jsonwebtoken.Claims;
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

        // 6. 生成双 Token
        String userId = user.getId().toString();
        String accessToken = JwtUtil.createJWT(userId); // AccessToken: 默认15分钟
        String refreshToken = JwtUtil.createRefreshToken(userId); // RefreshToken: 7天

        // 7. 将用户信息和 RefreshToken 存入 Redis
        long refreshTokenTtl = 7 * 24 * 60 * 60; // 7天(秒)
        redisUtil.set("login:" + userId, JSON.toJSONString(user), refreshTokenTtl);
        redisUtil.set("refresh:" + refreshToken, userId, refreshTokenTtl);

        // 8. 封装返回结果
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setNickName(user.getNickName());
        userInfoVO.setAvatar(user.getAvatar());
        userInfoVO.setSex(user.getSex() != null ? user.getSex().toString() : "2");
        userInfoVO.setEmail(user.getEmail());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setUserInfo(userInfoVO);

        return CommonResult.success(loginVO);
    }

    @Override
    public CommonResult<Void> logout() {
        try {
            // 1. 获取当前用户 ID
            Long userId = com.birdy.utils.SecurityUtils.getUserId();
            if (userId == null) {
                return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
            }

            // 2. 从 Redis 删除用户信息
            String loginKey = "login:" + userId;
            redisUtil.delete(loginKey);

            // 3. 将当前 AccessToken 加入黑名单(可选，防止 Token 被重用)
            // 获取当前请求的 Token
            org.springframework.web.context.request.ServletRequestAttributes attributes =
                (org.springframework.web.context.request.ServletRequestAttributes)
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                jakarta.servlet.http.HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    // 将 Token 加入黑名单(有效期设置为原 Token 的剩余时间，这里简化为15分钟)
                    long tokenTtl = jwtProperties.getTtl() / 1000; // 转换为秒
                    redisUtil.set("blacklist:" + token, "1", tokenTtl);
                }
            }

            return CommonResult.success();
        } catch (Exception e) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public CommonResult<LoginVO> refreshToken(String refreshToken) {
        try {
            // 1. 校验 RefreshToken 是否为空
            if (!StringUtils.hasText(refreshToken)) {
                return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
            }

            // 2. 解析 RefreshToken 获取用户 ID
            Claims claims = JwtUtil.parseJWT(refreshToken);
            String userId = claims.getSubject();

            // 3. 验证 RefreshToken 是否在 Redis 中存在
            String refreshKey = "refresh:" + refreshToken;
            Object storedUserId = redisUtil.get(refreshKey);
            if (storedUserId == null || !userId.equals(storedUserId.toString())) {
                return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
            }

            // 4. 查询用户信息
            User user = userMapper.selectById(Long.parseLong(userId));
            if (user == null || user.getDeleted()) {
                return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
            }

            // 5. 生成新的 AccessToken 和 RefreshToken
            String newAccessToken = JwtUtil.createJWT(userId);
            String newRefreshToken = JwtUtil.createRefreshToken(userId);

            // 6. 删除旧的 RefreshToken，存储新的 RefreshToken
            redisUtil.delete(refreshKey);
            long refreshTokenTtl = 7 * 24 * 60 * 60; // 7天(秒)
            redisUtil.set("refresh:" + newRefreshToken, userId, refreshTokenTtl);

            // 7. 更新用户信息缓存
            redisUtil.set("login:" + userId, JSON.toJSONString(user), refreshTokenTtl);

            // 8. 封装返回结果
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setId(user.getId());
            userInfoVO.setNickName(user.getNickName());
            userInfoVO.setAvatar(user.getAvatar());
            userInfoVO.setSex(user.getSex() != null ? user.getSex().toString() : "2");
            userInfoVO.setEmail(user.getEmail());

            LoginVO loginVO = new LoginVO();
            loginVO.setToken(newAccessToken);
            loginVO.setRefreshToken(newRefreshToken);
            loginVO.setUserInfo(userInfoVO);

            return CommonResult.success(loginVO);
        } catch (Exception e) {
            return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
        }
    }
}
