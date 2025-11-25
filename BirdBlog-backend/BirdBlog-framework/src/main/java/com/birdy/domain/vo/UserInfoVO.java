package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    
    /**
     * 用户角色列表
     */
    private List<RoleVO> roles;

    /**
     * 用户权限标识列表
     */
    private List<String> permissions;
}
