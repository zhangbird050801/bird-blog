package com.birdblog.admin;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Birdy
 * @date 2025/3/8 13:37
 * @description AdminApplication
 */
@SpringBootApplication(scanBasePackages = {
        "com.birdblog.framework",
        "com.birdblog.admin"
})
@EnableKnife4j
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
