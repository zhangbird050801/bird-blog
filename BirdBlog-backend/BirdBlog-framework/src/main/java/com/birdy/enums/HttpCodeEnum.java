package com.birdy.enums;

import lombok.Getter;

/**
 * @author Birdy
 * @date 2025/10/26 13:10
 * @description HttpCodeEnum 封装 code 和 msg
 */
@Getter
public enum HttpCodeEnum {

    SUCCESS(200, "操作成功"),
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"系统出现异常"),
    USERNAME_EXIST(501,"用户名已存在"),
    PHONE_NUMBER_EXIST(502,"手机号已存在"),
    EMAIL_EXIST(503, "邮箱已存在"),
    // 注册相关错误
    REGISTER_REQUIRE_FIELDS(508, "用户名、密码和邮箱不能为空"),
    PASSWORD_TOO_SHORT(509, "密码长度不能少于6位"),
    CAPTCHA_REQUIRED(510, "验证码不能为空"),
    REGISTER_FAILED(511, "注册失败，请稍后重试"),
    REQUIRE_USERNAME(504, "必需填写用户名"),
    LOGIN_ERROR(505,"用户名或密码错误"),

    USER_BANNED(507, "账号已被停用"),

    // 验证码相关错误码
    CAPTCHA_ERROR_OR_EXPIRE(506, "验证码错误或已过期"),
    
    // 文章相关错误码
    ARTICLE_NOT_FOUND(404, "文章不存在或已下架"),
    ARTICLE_ID_NOT_NULL(400, "文章ID不能为空"),
    ARTICLE_SLUG_NOT_NULL(400, "文章slug不能为空"),

    KEYWORDS_EMPTY(507, "搜索关键词不能为空"),

    // 通用参数错误码
    PARAM_ERROR(400, "参数错误"),

    // 通用未找到错误码
    NOT_FOUND(404, "资源不存在");

    private final int code;
    private final String msg;

    HttpCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
