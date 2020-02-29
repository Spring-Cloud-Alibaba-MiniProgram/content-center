package com.itchen.contentcenter.test;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.itchen.contentcenter.dao.share.ShareMapper;
import com.itchen.contentcenter.domain.dto.user.UserDTO;
import com.itchen.contentcenter.domain.entity.share.Share;
import com.itchen.contentcenter.feignclient.TestBaiduFeignClient;
import com.itchen.contentcenter.feignclient.TestUserCenterFeignClient;
import com.itchen.contentcenter.sentineltest.TestControllerBlockHandlerClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 测试 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-05
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("test")
    public List<Share> testInsert() {
        // 1. 插入
        Share share = new Share();
        share.setTitle("xxx");
        share.setCover("xxx");
        share.setAuditStatus("chen");
        share.setBuyCount(1);
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        this.shareMapper.insertSelective(share);
        // 2. 查询
        List<Share> shares = this.shareMapper.selectAll();
        return shares;
    }

    /**
     * 测试：服务发现，证明内容中心总能找到用户中心
     *
     * @return 用户中心所有实例的地址信息
     */
    @GetMapping("/test2")
    public List<ServiceInstance> getInstances() {
        // 查询指定服务的所有实例的信息
        return this.discoveryClient.getInstances("user-center");
    }

    @Autowired
    private TestUserCenterFeignClient testUserCenterFeignClient;

    @GetMapping(value = "/test-get")
    public UserDTO query(UserDTO userDTO) {
        return testUserCenterFeignClient.query(userDTO);
    }

    @Autowired
    private TestBaiduFeignClient testBaiduFeignClient;

    @GetMapping("baidu")
    public String baiduIndex() {
        return testBaiduFeignClient.index();
    }

    @Autowired
    private TestService testService;

    @GetMapping("test-a")
    public String testA() {
        testService.common();
        return "test-a";
    }

    @GetMapping("test-b")
    public String testB() {
        testService.common();
        return "test-b";
    }

    @GetMapping("test-hot")
    @SentinelResource("hot")
    public String testHot(@RequestParam(required = false) String a,
                          @RequestParam(required = false) String b) {
        return a + " " + b;
    }

    @GetMapping("/test-sentinel-api")
    public String testSentinelAPI(@RequestParam(required = false) String a) {
        // 定义一个 sentinel 保护的资源，名称是 test-sentinel-api
        String resourceName = "test-sentinel-api";
        ContextUtil.enter(resourceName, "test-wfw");
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);
            // 被保护的业务逻辑
            if (StringUtils.isBlank(a)) {
                throw new IllegalArgumentException("参数不能为空");
            }
            return a;
        } catch (BlockException e) {
            // 如果被保护的资源被限流或者降级了，就会抛 BlockException
            log.warn("限流，或者降级了", e);
            return "限流，或者降级了";
        } catch (IllegalArgumentException e) {
            // 统计 IllegalArgumentException【发生次数、发生占比...】
            Tracer.trace(e);
            return "参数非法！";
        } finally {
            if (entry != null) {
                // 退出
                entry.exit();
            }
            ContextUtil.exit();
        }

    }

    @GetMapping("/test-sentinel-resource")
    @SentinelResource(
            value = "test-sentinel-api",
            blockHandler = "block",
            blockHandlerClass = TestControllerBlockHandlerClass.class,
            fallback = "fallback")
    public String testSentinelResource(@RequestParam(required = false) String a) {
        // 定义一个 sentinel 保护的资源，名称是 test-sentinel-api
        // 被保护的业务逻辑
        if (StringUtils.isBlank(a)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return a;
    }

    // /**
    //  * 处理限流或者降级
    //  *
    //  * @param a
    //  * @param e
    //  * @return
    //  */
    // public String block(String a, BlockException e) {
    //     log.warn("限流，或者降级了 block", e);
    //     return "限流，或者降级了 block";
    // }

    /**
     * 1.5 处理降级
     * - Sentinel 1.6 可以处理 Throwable，支持 fallbackClass
     *
     * @param a
     * @return
     */
    public String fallback(String a) {
        return "限流，或者降级了 fallback";
    }

    @Autowired
    private RestTemplate restTemplate;

    /**
     * RestTemplate 整合 Sentinel
     *
     * @param userId
     * @return
     */
    @GetMapping("test-rest-template-sentinel/{userId}")
    public UserDTO test(@PathVariable String userId) {
        return this.restTemplate.getForObject("http://user-center/users/{userId}", UserDTO.class, userId);
    }

    /**
     * RestTemplate 传递 Token
     *
     * @param userId
     * @return
     */
    @GetMapping("tokenRelay/{userId}")
    public ResponseEntity<UserDTO> tokenRelay(@PathVariable String userId, @RequestHeader("X-Token") String xToken, HttpServletRequest request) {
        String token = request.getHeader("X-Token");
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Token", token);

        return this.restTemplate.exchange(
                "http://user-center/users/{userId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDTO.class,
                userId
        );

    }

    @Autowired
    private Source source;

    @GetMapping("test-stream")
    public String testStream() {
        this.source.output()
                .send(
                        MessageBuilder
                                .withPayload("消息体")
                                .build()
                );
        return "success";
    }

}
