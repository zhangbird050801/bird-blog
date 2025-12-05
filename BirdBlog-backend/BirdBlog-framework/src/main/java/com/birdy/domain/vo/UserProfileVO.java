package com.birdy.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户个人信息VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：0男，1女，2未知
     */
    private Integer sex;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 收藏文章数量
     */
    private Long favoriteCount;

    /**
     * 点赞文章数量
     */
    private Long likeCount;
}