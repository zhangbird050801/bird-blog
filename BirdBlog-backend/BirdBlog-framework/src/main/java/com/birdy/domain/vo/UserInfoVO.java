package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息 VO
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 返回给前端的用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别:0男,1女,2未知
     */
    private String sex;

    /**
     * 邮箱
     */
    private String email;
}
