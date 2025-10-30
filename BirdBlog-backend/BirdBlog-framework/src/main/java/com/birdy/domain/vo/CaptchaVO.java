package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码 VO
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 验证码响应数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {

    /**
     * 验证码图片 Base64 编码
     */
    private String img;

    /**
     * 验证码 UUID(用于提交时验证)
     */
    private String uuid;
}
