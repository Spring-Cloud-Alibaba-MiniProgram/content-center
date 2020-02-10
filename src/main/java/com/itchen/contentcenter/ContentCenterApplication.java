package com.itchen.contentcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 内容中心启动类 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-05
 */
@MapperScan("com.itchen.contentcenter.dao")
// @EnableFeignClients(defaultConfiguration = GlobalFeignConfiguration.class)
@EnableFeignClients
@SpringBootApplication
@EnableBinding({Source.class})
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }

}
