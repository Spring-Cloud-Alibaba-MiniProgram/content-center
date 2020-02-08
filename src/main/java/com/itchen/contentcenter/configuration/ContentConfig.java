package com.itchen.contentcenter.configuration;

import org.springframework.cloud.alibaba.sentinel.annotation.SentinelRestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 配置 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-06
 */
@Component
public class ContentConfig {

    /**
     * 在 Spring 容器中，创建一个对象，类型 RestTemplate；名称/ID 是：restTemplate
     * <bean id="restTemplate" class="xxx.RestTemplate"/>
     *
     * @return
     */
    @Bean
    @LoadBalanced
    @SentinelRestTemplate
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
