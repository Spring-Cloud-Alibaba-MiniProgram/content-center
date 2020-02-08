package com.itchen.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 扩展 Sentinel —— RESTful URL 支持 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-08
 */
@Component
@Slf4j
public class MyUrlCleaner implements UrlCleaner {
    @Override
    public String clean(String originUrl) {
        // 让 /shares/1 与 /shares/2 的返回值相同
        // 返回 /shares/{number}

        String[] split = originUrl.split("/");
        return Arrays.stream(split)
                .map(string -> {
                    if (NumberUtils.isNumber(string)) {
                        return "{number}";
                    }
                    return string;
                })
                .reduce((a, b) -> a + "/" + b)
                .orElse("");
    }
}
