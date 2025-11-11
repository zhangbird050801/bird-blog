package com.birdy.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * 友链申请请求 DTO
 *
 * @author Birdy
 * @date 2025/11/11
 * @description 游客申请友链时使用的请求参数
 */
@Data
public class LinkApplicationRequest {

    /**
     * 网站名称（必填）
     */
    @NotBlank(message = "网站名称不能为空")
    @Size(max = 50, message = "网站名称不能超过50个字符")
    private String name;

    /**
     * 网站Logo地址
     */
    @Pattern(regexp = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*\\/?$", message = "Logo地址格式不正确")
    private String logo;

    /**
     * 网站描述（必填）
     */
    @NotBlank(message = "网站描述不能为空")
    @Size(max = 200, message = "网站描述不能超过200个字符")
    private String description;

    /**
     * 网站地址（必填）
     */
    @NotBlank(message = "网站地址不能为空")
    @Pattern(regexp = "^https?://([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*\\/?$", message = "网站地址格式不正确")
    private String url;

    /**
     * 联系邮箱
     */
    @Size(max = 100, message = "联系邮箱不能超过100个字符")
    private String contactEmail;

    /**
     * 联系人姓名
     */
    @Size(max = 50, message = "联系人姓名不能超过50个字符")
    private String contactName;

    /**
     * 申请留言
     */
    @Size(max = 500, message = "申请留言不能超过500个字符")
    private String message;
}