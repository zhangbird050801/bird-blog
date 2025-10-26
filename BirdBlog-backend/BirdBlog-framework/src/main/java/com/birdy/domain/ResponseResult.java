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
 * @description ResponseResult 统一响应结果封装类
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public ResponseResult() {
        this(HttpCodeEnum.SUCCESS);
    }

    public ResponseResult(HttpCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }

    public ResponseResult(HttpCodeEnum codeEnum, T data) {
        this(codeEnum);
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(HttpCodeEnum.SUCCESS);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(HttpCodeEnum.SUCCESS, data);
    }

    public static <T> ResponseResult<T> success(String msg, T data) {
        return new ResponseResult<>(HttpCodeEnum.SUCCESS.getCode(), msg, data);
    }

    public static <T> ResponseResult<T> error(HttpCodeEnum codeEnum) {
        return new ResponseResult<>(codeEnum);
    }

    public static <T> ResponseResult<T> error(HttpCodeEnum codeEnum, String msg) {
        return new ResponseResult<>(codeEnum.getCode(), msg);
    }
}
