package com.itchen.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 测试 Feign 脱离 Ribbon 使用 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-07
 */
@FeignClient(name = "baidu", url = "http://www.baidu.com")
public interface TestBaiduFeignClient {

    @GetMapping("")
    String index();

}
