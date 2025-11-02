package com.birdy.controller;

import com.birdy.domain.CommonResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author Birdy
 * @date 2025/11/2 18:11
 * @description AdminArticleController
 */
@RestController
@RequestMapping("/article")
public class AdminArticleController {
    @PostMapping
    public CommonResult<?> add() {
        return CommonResult.success();
    }

    @PutMapping("/{id}")
    public CommonResult<?> update() {
        return CommonResult.success();
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete() {
        return CommonResult.success();
    }


}
