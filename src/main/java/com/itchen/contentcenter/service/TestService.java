package com.itchen.contentcenter.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Sentinel 测试流控模式-链路 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-07
 */
@Slf4j
@Service
public class TestService {

    @SentinelResource("common")
    public String common() {
        log.info("common...");
        return "common";
    }

}
