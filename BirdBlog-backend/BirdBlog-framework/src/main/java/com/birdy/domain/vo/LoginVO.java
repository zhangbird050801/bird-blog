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
     * JWT Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;
}
