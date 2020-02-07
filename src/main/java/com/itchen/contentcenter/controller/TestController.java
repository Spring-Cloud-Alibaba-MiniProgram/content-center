package com.itchen.contentcenter.controller;

import com.itchen.contentcenter.dao.share.ShareMapper;
import com.itchen.contentcenter.domain.dto.user.UserDTO;
import com.itchen.contentcenter.domain.entity.share.Share;
import com.itchen.contentcenter.feignclient.TestBaiduFeignClient;
import com.itchen.contentcenter.feignclient.TestUserCenterFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 测试 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-05
 */
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

}
