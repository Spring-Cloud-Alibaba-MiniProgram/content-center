package com.itchen.contentcenter.test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * RestTemplate 拦截器，在请求头添加 X-Token .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-29
 */
public class TestRestTemplateTokenRelayInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 从 header 里面获取 token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest httpRequest = servletRequestAttributes.getRequest();
        String token = httpRequest.getHeader("X-Token");

        HttpHeaders headers = request.getHeaders();
        headers.add("X-Token", token);
        // 保证请求继续执行
        return execution.execute(request, body);
    }
}
