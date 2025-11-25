package com.birdy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.birdy.config.JwtProperties;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.LoginRequestDTO;
import com.birdy.domain.dto.RegisterRequestDTO;
import com.birdy.domain.entity.User;
import com.birdy.domain.entity.SysRole;
import com.birdy.domain.entity.SysUserRole;
import com.birdy.domain.vo.LoginVO;
import com.birdy.domain.vo.RegisterVO;
import com.birdy.domain.vo.UserInfoVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.enums.LoginScene;
import com.birdy.mapper.UserMapper;
import com.birdy.mapper.SysRoleMapper;
import com.birdy.mapper.SysUserRoleMapper;
import com.birdy.service.LoginService;
import com.birdy.utils.CaptchaUtil;
import com.birdy.utils.JwtUtil;
import com.birdy.utils.RedisUtil;
import com.birdy.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.birdy.constants.SysConstants.*;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public CommonResult<LoginVO> login(LoginRequestDTO loginRequestDTO, LoginScene loginScene) {
        LoginScene scene = loginScene == null ? LoginScene.FRONT : loginScene;
        // 1. 校验参数
        if (!StringUtils.hasText(loginRequestDTO.getUserName()) || !StringUtils.hasText(loginRequestDTO.getPassword())) {
            return CommonResult.error(HttpCodeEnum.REQUIRE_USERNAME);
        }

        // 2. 校验验证码
        if (StringUtils.hasText(loginRequestDTO.getCode()) && StringUtils.hasText(loginRequestDTO.getUuid())) {
            if (!captchaUtil.verifyCaptcha(loginRequestDTO.getUuid(), loginRequestDTO.getCode())) {
                return CommonResult.error(HttpCodeEnum.CAPTCHA_ERROR_OR_EXPIRE);
            }
        }

        // 3. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginRequestDTO.getUserName());
        queryWrapper.eq(User::getDeleted, USER_NOT_DELETED);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return CommonResult.error(HttpCodeEnum.LOGIN_ERROR);
        }

        // 4. 校验密码
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            return CommonResult.error(HttpCodeEnum.LOGIN_ERROR);
        }

        // 5. 校验用户状态
        if (user.getStatus() != null && user.getStatus() == 1) {
            return CommonResult.error(HttpCodeEnum.USER_BANNED);
        }

        // 5.1 根据登录场景校验权限 - 通过角色检查而非type字段
        if (scene == LoginScene.ADMIN) {
            // 检查用户是否有管理员权限（超级管理员、编辑、审核员可以登录后台）
            boolean hasAdminAccess = hasAdminRole(user.getId());
            if (!hasAdminAccess) {
                return CommonResult.error(HttpCodeEnum.NO_OPERATOR_AUTH, "访客用户不能登录后台");
            }
        }

        // 6. 生成双 Token
        String userId = user.getId().toString();
        String accessToken = JwtUtil.createJWT(userId);
        String refreshToken = JwtUtil.createRefreshToken(userId);

        // 7. 缓存并返回封装结果
        cacheLogin(user, refreshToken);
        LoginVO loginVO = buildLoginVO(user, accessToken, refreshToken);
        return CommonResult.success(loginVO);
    }

    @Override
    public CommonResult<Void> logout() {
        try {
            // 1. 获取当前用户 ID
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
            }

            // 2. 从 Redis 删除用户信息
            String loginKey = "login:" + userId;
            redisUtil.delete(loginKey);

            // 3. 将当前 AccessToken 加入黑名单
            // 获取当前请求的 Token
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    // 将 Token 加入黑名单(有效期设置为原 Token 的剩余时间) 转换为秒
                    long tokenTtl = jwtProperties.getTtl() / 1000;
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

            // 6. 删除旧的 RefreshToken，存储新的 RefreshToken 并更新缓存
            redisUtil.delete(refreshKey);
            cacheLogin(user, newRefreshToken);

            // 7. 封装返回结果
            LoginVO loginVO = buildLoginVO(user, newAccessToken, newRefreshToken);
            return CommonResult.success(loginVO);
        } catch (Exception e) {
            return CommonResult.error(HttpCodeEnum.NEED_LOGIN);
        }
    }

    @Override
    public CommonResult<RegisterVO> register(RegisterRequestDTO registerRequestDTO) {
        // 1. 校验参数
        if (!StringUtils.hasText(registerRequestDTO.getUserName()) 
                || !StringUtils.hasText(registerRequestDTO.getPassword())
                || !StringUtils.hasText(registerRequestDTO.getEmail())) {
            return CommonResult.error(HttpCodeEnum.REGISTER_REQUIRE_FIELDS);
        }

        // 2. 校验验证码
        if (!StringUtils.hasText(registerRequestDTO.getCode()) || !StringUtils.hasText(registerRequestDTO.getUuid())) {
            return CommonResult.error(HttpCodeEnum.CAPTCHA_REQUIRED);
        }

        if (!captchaUtil.verifyCaptcha(registerRequestDTO.getUuid(), registerRequestDTO.getCode())) {
            return CommonResult.error(HttpCodeEnum.CAPTCHA_ERROR_OR_EXPIRE);
        }

        // 3. 校验用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, registerRequestDTO.getUserName());
        usernameWrapper.eq(User::getDeleted, false);
        Long usernameCount = userMapper.selectCount(usernameWrapper);
        if (usernameCount > 0) {
            return CommonResult.error(HttpCodeEnum.USERNAME_EXIST);
        }

        // 4. 校验邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, registerRequestDTO.getEmail());
        emailWrapper.eq(User::getDeleted, false);
        Long emailCount = userMapper.selectCount(emailWrapper);
        if (emailCount > 0) {
            return CommonResult.error(HttpCodeEnum.EMAIL_EXIST);
        }

        // 5. 密码长度校验
        if (registerRequestDTO.getPassword().length() < PASSWORD_MIN_LENGTH) {
            return CommonResult.error(HttpCodeEnum.PASSWORD_TOO_SHORT);
        }

        // 6. 创建新用户
        User newUser = new User();
        newUser.setUsername(registerRequestDTO.getUserName());
        // 默认昵称与用户名相同
        newUser.setNickName(registerRequestDTO.getUserName());
        newUser.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        newUser.setEmail(registerRequestDTO.getEmail());
        // 移除type字段设置，新用户默认为访客角色，通过角色表管理
        newUser.setStatus(USER_STATUS_NORMAL);
        newUser.setSex(USER_SEX_UNKNOWN);
        newUser.setDeleted(USER_NOT_DELETED);
        newUser.setCreator(SYSTEM);
        newUser.setUpdater(SYSTEM);

        // 7. 保存到数据库
        int rows = userMapper.insert(newUser);
        if (rows <= 0) {
            return CommonResult.error(HttpCodeEnum.REGISTER_FAILED);
        }

        // 8. 封装返回结果
        RegisterVO registerVO = new RegisterVO();
        BeanUtil.copyProperties(newUser, registerVO);
        registerVO.setUserName(newUser.getUsername());
        return CommonResult.success(registerVO);
    }

    /**
     * 缓存登录信息和 refresh token
     */
    private void cacheLogin(User user, String refreshToken) {
        long ttl = REFRESH_TOKEN_TTL_SECONDS;
        redisUtil.set("login:" + user.getId(), JSON.toJSONString(user), ttl);
        redisUtil.set("refresh:" + refreshToken, user.getId().toString(), ttl);
    }

    /**
     * 构建 LoginVO
     */
    private LoginVO buildLoginVO(User user, String accessToken, String refreshToken) {
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(user, userInfoVO);
        userInfoVO.setSex(user.getSex() != null ? user.getSex().toString() : String.valueOf(USER_SEX_UNKNOWN));
        // 移除type字段设置，通过角色来管理用户权限

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setUserInfo(userInfoVO);
        return loginVO;
    }

    /**
     * 检查用户是否有管理员权限（可以登录后台）
     * 超级管理员、编辑、审核员可以登录后台
     * 访客不能登录后台
     *
     * @param userId 用户ID
     * @return 是否有管理员权限
     */
    private boolean hasAdminRole(Long userId) {
        try {
            // 1. 查询用户的角色
            LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(SysUserRole::getUserId, userId)
                    .eq(SysUserRole::getDeleted, false);

            List<SysUserRole> userRoles = sysUserRoleMapper.selectList(userRoleWrapper);
            if (userRoles.isEmpty()) {
                // 如果没有角色，默认为访客，不能登录后台
                return false;
            }

            // 2. 获取角色ID列表
            List<Long> roleIds = userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .collect(Collectors.toList());

            // 3. 查询角色信息
            LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(SysRole::getId, roleIds)
                    .eq(SysRole::getStatus, 0)  // 状态正常
                    .eq(SysRole::getDeleted, false);

            List<SysRole> roles = sysRoleMapper.selectList(roleWrapper);
            if (roles.isEmpty()) {
                return false;
            }

            // 4. 检查是否有管理员角色
            // 超级管理员、编辑、审核员可以登录后台
            return roles.stream().anyMatch(role -> {
                String roleCode = role.getCode();
                return "SUPER_ADMIN".equals(roleCode)
                    || "EDITOR".equals(roleCode)
                    || "REVIEWER".equals(roleCode);
            });

        } catch (Exception e) {
            // 异常情况下，拒绝访问
            return false;
        }
    }
}
