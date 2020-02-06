package com.itchen.contentcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 内容中心启动类 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-05
 */
@MapperScan("com.itchen")
@SpringBootApplication
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }

}