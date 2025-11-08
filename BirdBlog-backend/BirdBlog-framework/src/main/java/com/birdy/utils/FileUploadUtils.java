package com.birdy.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 文件上传工具类
 *
 * @author Birdy
 * @date 2025/11/8
 */
public class FileUploadUtils {

    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 允许的文件类型
     */
    private static final String[] ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // 文档
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt",
            // 压缩文件
            "zip", "rar", "7z",
            // 音频
            "mp3", "wav",
            // 视频
            "mp4", "avi"
    };

    /**
     * 以默认配置上传文件
     *
     * @param file 上传的文件
     * @return 文件路径
     * @throws Exception 文件大小超限异常
     */
    public static final String upload(MultipartFile file) throws Exception {
        return upload(file, DEFAULT_MAX_SIZE);
    }

    /**
     * 根据文件路径上传文件
     *
     * @param file 上传的文件
     * @param maxSize 最大大小
     * @return 文件路径
     * @throws Exception 文件大小超限异常
     */
    public static final String upload(MultipartFile file, long maxSize) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("上传文件不能为空");
        }

        if (file.getSize() > maxSize) {
            throw new Exception("上传文件大小超出限制");
        }

        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        if (!isValidExtension(extension)) {
            throw new Exception("文件类型不正确");
        }

        return fileName;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = "";
        String fileName = file.getOriginalFilename();
        if (StringUtils.hasLength(fileName)) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex + 1);
            }
        }
        return extension;
    }

    /**
     * 判断是否为允许的文件类型
     *
     * @param extension 文件扩展名
     * @return 是否允许
     */
    public static boolean isValidExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        return Arrays.asList(ALLOWED_EXTENSION).contains(extension.toLowerCase());
    }

    /**
     * 获取文件名的后缀
     *
     * @param filename 文件名
     * @return 后缀名
     */
    public static final String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}