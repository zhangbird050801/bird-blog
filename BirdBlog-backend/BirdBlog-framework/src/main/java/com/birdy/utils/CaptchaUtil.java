package com.birdy.utils;

import com.wf.captcha.SpecCaptcha;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 验证码工具类
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 基于 easy-captcha 生成图形验证码
 */
@Component
public class CaptchaUtil {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 验证码在 Redis 中的 key 前缀
     */
    private static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 验证码过期时间(秒) - 5分钟
     */
    private static final long CAPTCHA_EXPIRATION = 5 * 60;

    /**
     * 生成验证码
     *
     * @return 验证码图片 Base64 编码和 UUID
     */
    public CaptchaResult generateCaptcha() {
        // 生成验证码对象(宽度 130,高度 48,验证码长度 4)
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        
        // 获取验证码文本
        String code = captcha.text();
        
        // 获取验证码图片 Base64 编码
        String imageBase64 = captcha.toBase64();
        
        // 生成 UUID 作为验证码的唯一标识
        String uuid = UUID.randomUUID().toString();
        
        // 将验证码存入 Redis,设置过期时间
        String redisKey = CAPTCHA_PREFIX + uuid;
        redisUtil.set(redisKey, code.toLowerCase(), CAPTCHA_EXPIRATION);
        
        return new CaptchaResult(imageBase64, uuid);
    }

    /**
     * 验证验证码
     *
     * @param uuid 验证码 UUID
     * @param code 用户输入的验证码
     * @return true 验证通过,false 验证失败
     */
    public boolean verifyCaptcha(String uuid, String code) {
        if (uuid == null || code == null) {
            return false;
        }
        
        String redisKey = CAPTCHA_PREFIX + uuid;
        String cachedCode = (String) redisUtil.get(redisKey);
        
        // 验证后删除验证码(一次性使用)
        if (cachedCode != null) {
            redisUtil.delete(redisKey);
            return cachedCode.equalsIgnoreCase(code.trim());
        }
        
        return false;
    }

    /**
     * 验证码结果内部类
     */
    @Getter
    @AllArgsConstructor
    public static class CaptchaResult {
        private final String imageBase64;
        private final String uuid;

//        public CaptchaResult(String imageBase64, String uuid) {
//            this.imageBase64 = imageBase64;
//            this.uuid = uuid;
//        }

    }
}
