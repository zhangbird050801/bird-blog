package com.birdy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 *
 * @author Birdy
 * @date 2025/10/30
 * @description 封装 Redis 常用操作
 */
@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 构造函数注入 RedisTemplate
     *
     * @param redisTemplate Redis 模板
     */
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 设置缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 是否成功
     */
    public boolean expire(String key, long time) {
        if (time > 0) {
            return redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
        return false;
    }

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键,不能为 null
     * @return 时间(秒),返回 0 代表永久有效或不存在/无过期
     */
    public long getExpire(String key) {
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (expire == null || expire < 0) {
            return 0L;
        }
        return expire;
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true 存在,false 不存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值或多个
     */
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    /**
     * 获取普通缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取普通缓存(泛型方法)
     * Redis 中的数据必须通过 Jackson2JsonRedisSerializer 或类似的 JSON 序列化器存储,
     * 确保可以成功反序列化为指定类型
     *
     * @param key   键
     * @param clazz 返回值类型
     * @param <T>   泛型类型
     * @return 指定类型的值
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                // 自动转换成指定类型
                return clazz.cast(value);
            }
        } catch (Exception e) {
            logger.error("Redis get error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true 成功,false 失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("Redis set error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒),time 要大于 0,如果 time 小于等于 0 将设置无限期
     * @return true 成功,false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("Redis set with time error: {}", e.getMessage(), e);
            return false;
        }
    }
}
