package com.birdy.domain.vo;

import lombok.Data;

import java.util.Date;

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
    private Integer type;
    private Integer sex;
    private Date createTime;
    private Date updateTime;
}
