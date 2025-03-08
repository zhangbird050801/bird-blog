package com.birdblog.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Birdy
 * @date 2025/3/8 13:32
 * @description TestController
 */
@RestController
@Tag(name = "测试接口", description = "用于验证文档配置")
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "健康检查", description = "最简单的测试接口")
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}