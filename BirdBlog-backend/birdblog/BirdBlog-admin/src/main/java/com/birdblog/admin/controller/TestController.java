package com.birdblog.admin.controller;

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
@Tag(name = "管理测试接口")
@RequestMapping("/admin/test")
public class TestController {

    @Operation(summary = "管理员测试接口")
    @GetMapping("/adminCheck")
    public String adminCheck() {
        return "Admin OK";
    }
}