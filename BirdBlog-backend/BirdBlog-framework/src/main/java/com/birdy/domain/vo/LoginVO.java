package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 VO
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 登录成功返回的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /**
     * 访问令牌(AccessToken)，有效期15分钟
     */
    private String token;

    /**
     * 刷新令牌(RefreshToken)，有效期7天
     */
    private String refreshToken;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;
}
