package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册响应 VO
 *
 * @author Birdy
 * @date 2025/11/2 18:19
 * @description 用户注册成功后返回的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVO {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称（默认与用户名相同）
     */
    private String nickName;
}
