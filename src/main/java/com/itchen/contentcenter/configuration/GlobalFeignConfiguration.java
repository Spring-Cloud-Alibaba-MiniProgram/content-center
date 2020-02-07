package com.itchen.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;


/**
 * Feign 配置
 * Feign 的配置类别加 @Configuration 注解，否则必须挪到 @ComponentScan 能扫描的包以外 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-07
 */
public class GlobalFeignConfiguration {

    /**
     * 指定 Feign 日志级别
     *
     * @return
     */
    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }

}
