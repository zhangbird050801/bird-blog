package com.birdy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BirdBlog 管理后台启动类
 *
 * @author Birdy
 * @date 2025/10/31
 * @description 管理后台应用程序入口
 */
@SpringBootApplication
@MapperScan("com.birdy.mapper")
public class BirdBlogAdminApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BirdBlogAdminApplication.class, args);
        System.out.println("========================================");
        System.out.println("BirdBlog 管理后台启动成功！");
        System.out.println("访问地址: http://localhost:8989");
        System.out.println("========================================");
    }
}
