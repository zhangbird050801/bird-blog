package com.birdy.utils;

import com.birdy.config.CosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * 腾讯云COS工具类
 *
 * @author Birdy
 * @date 2025/11/8
 */
@Component
public class CosUtils {

    @Autowired
    private CosConfig cosConfig;

    private COSClient cosClient;

    /**
     * 获取COS客户端
     */
    private COSClient getCosClient() {
        if (cosClient == null) {
            synchronized (this) {
                if (cosClient == null) {
                    // 初始化用户身份信息(secretId, secretKey)
                    COSCredentials cred = new BasicCOSCredentials(cosConfig.getSecretId(), cosConfig.getSecretKey());

                    // 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
                    ClientConfig clientConfig = new ClientConfig(new Region(cosConfig.getRegion()));

                    // 生成cos客户端
                    cosClient = new COSClient(cred, clientConfig);
                }
            }
        }
        return cosClient;
    }

    /**
     * 上传图片到COS
     *
     * @param file 上传的文件
     * @param businessName 业务名称
     * @return 文件路径key
     * @throws Exception 上传异常
     */
    public String uploadImgToCos(MultipartFile file, String businessName) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("上传文件不能为空");
        }

        // 检查文件大小
        int imageSize = Integer.parseInt(cosConfig.getImageSize());
        int maxSize = imageSize << 20;
        if (file.getSize() > maxSize) {
            throw new Exception("上传文件大小不能超过" + imageSize + "M！");
        }

        if (StringUtils.isEmpty(businessName)) {
            businessName = cosConfig.getCommon();
        }

        // 生成文件夹层级
        Calendar cale = Calendar.getInstance();
        int year = cale.get(Calendar.YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        Date dd = cale.getTime();
        String month = sdf.format(dd);
        String folderName = cosConfig.getProjectName() + "/image/" + businessName + "/" + year + "/" + month + "/";

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        Random random = new Random();
        String name;
        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            name = random.nextInt(10000) + System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            String extension = FileUploadUtils.getExtension(file);
            name = random.nextInt(10000) + System.currentTimeMillis() + "." + extension;
        }

        // 生成对象键
        String key = folderName + name;

        try (InputStream inputStream = file.getInputStream()) {
            // 设置对象元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), key, inputStream, metadata);

            // 执行上传
            PutObjectResult putObjectResult = getCosClient().putObject(putObjectRequest);

            return key;
        } catch (Exception e) {
            throw new Exception("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 以文件流方式上传图片
     *
     * @param is 输入流
     * @param originalFilename 原始文件名
     * @param fileSize 文件大小
     * @param businessName 业务名称
     * @return 文件路径key
     * @throws Exception 上传异常
     */
    public String uploadImgToCos(InputStream is, String originalFilename, long fileSize, String businessName) throws Exception {
        // 检查文件大小
        int imageSize = Integer.parseInt(cosConfig.getImageSize());
        int maxSize = imageSize << 20;
        if (fileSize > maxSize) {
            throw new Exception("上传文件大小不能超过" + imageSize + "M！");
        }

        if (StringUtils.isEmpty(businessName)) {
            businessName = cosConfig.getCommon();
        }

        // 生成文件夹层级
        Calendar cale = Calendar.getInstance();
        int year = cale.get(Calendar.YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        Date dd = cale.getTime();
        String month = sdf.format(dd);
        String folderName = cosConfig.getProjectName() + "/image/" + businessName + "/" + year + "/" + month + "/";

        // 生成文件名
        Random random = new Random();
        String name;
        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            name = random.nextInt(10000) + System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            name = random.nextInt(10000) + System.currentTimeMillis() + ".jpg";
        }

        // 生成对象键
        String key = folderName + name;

        try {
            // 设置对象元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), key, is, metadata);

            // 执行上传
            PutObjectResult putObjectResult = getCosClient().putObject(putObjectRequest);

            return key;
        } catch (Exception e) {
            throw new Exception("文件上传失败: " + e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                // 忽略关闭异常
            }
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param key 文件key
     * @return 访问URL
     */
    public String getFileUrl(String key) {
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        // 根据COS配置生成访问URL（私有读写需要使用签名URL）
        return "https://" + cosConfig.getBucketName() + ".cos." + cosConfig.getRegion() + ".myqcloud.com/" + key;
    }

    /**
     * 获取带签名的文件访问URL（适用于私有读写的桶）
     *
     * @param key 文件key
     * @return 带签名的访问URL
     */
    public String getSignedFileUrl(String key) {
        if (StringUtils.isEmpty(key)) {
            return "";
        }

        // 生成带签名的URL，有效期24小时（24*60*60*1000毫秒）
        java.util.Date expirationTime = new java.util.Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000);

        return getCosClient().generatePresignedUrl(
            cosConfig.getBucketName(),
            key,
            expirationTime
        ).toString();
    }
}