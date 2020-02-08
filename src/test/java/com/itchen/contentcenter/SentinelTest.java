package com.itchen.contentcenter;

import org.springframework.web.client.RestTemplate;

/**
 * Sentinel 测试流控模式 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-07
 */
public class SentinelTest {

    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        // 测试流控模式-关联
        // for (int i = 0; i < 1000; i++) {
        //     restTemplate.getForObject("http://localhost:8010/actuator/sentinel", String.class);
        //
        //     Thread.sleep(500);
        // }
        // 测试流控模式-排队等待
        for (int i = 0; i < 1000; i++) {
            String object = restTemplate.getForObject("http://localhost:8010/test-a", String.class);
            System.out.println("---" + object + "---");
        }
    }

}
