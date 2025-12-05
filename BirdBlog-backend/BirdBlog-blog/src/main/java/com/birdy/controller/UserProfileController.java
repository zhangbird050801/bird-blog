package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.UpdateUserInfoRequest;
import com.birdy.service.ArticleFavoriteService;
import com.birdy.service.ArticleLikeService;
import com.birdy.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

/**
 * 用户个人中心控制器
 */
@RestController
@RequestMapping("/user/profile")
@Tag(name = "用户个人中心", description = "用户个人中心相关接口")
public class UserProfileController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ArticleFavoriteService articleFavoriteService;
    
    @Autowired
    private ArticleLikeService articleLikeService;

    @GetMapping("/info")
    @Operation(summary = "获取用户个人信息")
    public CommonResult getUserInfo() {
        return userService.getCurrentUserInfo();
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户个人信息")
    public CommonResult updateUserInfo(@Valid @RequestBody UpdateUserInfoRequest request) {
        return userService.updateUserInfo(request);
    }

    @PostMapping("/avatar")
    @Operation(summary = "上传用户头像")
    public CommonResult uploadAvatar(@Parameter(description = "头像文件") @RequestParam("avatar") MultipartFile file) {
        return userService.uploadAvatar(file);
    }

    @GetMapping("/favorites")
    @Operation(summary = "获取用户收藏的文章")
    public CommonResult getFavoriteArticles(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return articleFavoriteService.getFavoriteArticles(pageNum, pageSize);
    }

    @GetMapping("/likes")
    @Operation(summary = "获取用户点赞的文章")
    public CommonResult getLikedArticles(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return articleLikeService.getLikedArticles(pageNum, pageSize);
    }
}