package com.itchen.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 扩展 Sentinel —— 实现区分来源 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-08
 */
// @Component
public class MyRequestOriginParser implements RequestOriginParser {

    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 从请求参数中获取名为 origin 的参数并返回
        // 如果获取不到 origin 参数，则抛异常

        // 实际项目中，来源参数放在 header 中
        String origin = request.getParameter("origin");
        if (StringUtils.isBlank(origin)) {
            throw new IllegalArgumentException("origin must be specified");
        }
        return origin;
    }
}
