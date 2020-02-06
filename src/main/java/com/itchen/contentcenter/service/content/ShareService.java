package com.itchen.contentcenter.service.content;

import com.itchen.contentcenter.dao.share.ShareMapper;
import com.itchen.contentcenter.domain.dto.content.ShareDTO;
import com.itchen.contentcenter.domain.dto.user.UserDTO;
import com.itchen.contentcenter.domain.entity.share.Share;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 分享服务 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-06
 */
@Service
@Slf4j
public class ShareService {

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    public ShareDTO findById(Integer id) {
        // 获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();
        // 怎么调用用户微服务的 /users/{id}

        // 用户中心所有实例的信息
        List<ServiceInstance> instances = this.discoveryClient.getInstances("user-center");
        String targetURL = instances.stream()
                .map(instance -> instance.getUri().toString() + "/users/{id}")
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("当前没有实例！"));

        log.info("请求的目标地址：{}", targetURL);
        UserDTO userDTO = restTemplate.getForObject(
                targetURL,
                UserDTO.class, userId
        );
        ShareDTO shareDTO = new ShareDTO();
        // 消息装配
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());

        return shareDTO;
    }


}
