package com.birdy.domain;

import com.birdy.enums.HttpCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Birdy
 * @date 2025/10/26 13:09
 * @description CommonResult 统一响应结果封装类
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResult<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public CommonResult() {
        this(HttpCodeEnum.SUCCESS);
    }

    public CommonResult(HttpCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }

    public CommonResult(HttpCodeEnum codeEnum, T data) {
        this(codeEnum);
        this.data = data;
    }

    public CommonResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CommonResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> CommonResult<T> success() {
        return new CommonResult<>(HttpCodeEnum.SUCCESS);
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(HttpCodeEnum.SUCCESS, data);
    }

    public static <T> CommonResult<T> success(String msg, T data) {
        return new CommonResult<>(HttpCodeEnum.SUCCESS.getCode(), msg, data);
    }

    public static <T> CommonResult<T> error(HttpCodeEnum codeEnum) {
        return new CommonResult<>(codeEnum);
    }

    public static <T> CommonResult<T> error(HttpCodeEnum codeEnum, String msg) {
        return new CommonResult<>(codeEnum.getCode(), msg);
    }
}
