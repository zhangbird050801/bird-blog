package com.birdy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.birdy.mapper")
public class BirdBlogApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(BirdBlogApplication.class, args);
    }
}
