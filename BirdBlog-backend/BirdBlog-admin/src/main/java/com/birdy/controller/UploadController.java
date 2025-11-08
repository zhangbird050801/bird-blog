package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.utils.CosUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 *
 * @author Birdy
 * @date 2025/11/8
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private CosUtils cosUtils;

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @param businessName 业务名称（可选）
     * @return 上传结果
     */
    @PostMapping("/image")
    public CommonResult<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "businessName", required = false, defaultValue = "article") String businessName) {

        try {
            // 上传到COS
            String fileKey = cosUtils.uploadImgToCos(file, businessName);

            // 生成带签名的访问URL（私有读写需要签名）
            String fileUrl = cosUtils.getSignedFileUrl(fileKey);

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("key", fileKey);
            result.put("fileName", file.getOriginalFilename());

            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传缩略图
     *
     * @param file 图片文件
     * @return 上传结果
     */
    @PostMapping("/thumbnail")
    public CommonResult<Map<String, String>> uploadThumbnail(@RequestParam("file") MultipartFile file) {
        try {
            // 上传到COS，使用thumbnail作为业务名称
            String fileKey = cosUtils.uploadImgToCos(file, "thumbnail");

            // 生成带签名的访问URL（私有读写需要签名）
            String fileUrl = cosUtils.getSignedFileUrl(fileKey);

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("key", fileKey);
            result.put("fileName", file.getOriginalFilename());

            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "缩略图上传失败: " + e.getMessage());
        }
    }
}