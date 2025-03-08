package com.birdblog.blog;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Birdy
 * @date 2025/3/8 13:37
 * @description BlogApplication
 */
@EnableKnife4j
@SpringBootApplication(scanBasePackages = {
        "com.birdblog.framework",
        "com.birdblog.blog"
})
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
