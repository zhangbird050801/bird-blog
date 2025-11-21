package com.birdy.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 后台用户管理 VO
 */
@Data
public class AdminUserVO {
    private Long id;
    private String username;
    private String nickName;
    private String email;
    private String phone;
    private Integer status;
    private Integer sex;
    private Date createTime;
    private Date updateTime;

    /**
     * 用户角色列表
     */
    private List<RoleVO> roles;
}
