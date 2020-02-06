package com.itchen.contentcenter.service.content;

import com.itchen.contentcenter.dao.share.ShareMapper;
import com.itchen.contentcenter.domain.dto.content.ShareDTO;
import com.itchen.contentcenter.domain.dto.user.UserDTO;
import com.itchen.contentcenter.domain.entity.share.Share;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 分享服务 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-06
 */
@Service
public class ShareService {

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private RestTemplate restTemplate;

    public ShareDTO findById(Integer id) {
        // 获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();
        // 怎么调用用户微服务的 /users/{id}

        UserDTO userDTO = restTemplate.getForObject(
                "http://localhost:8080/users/{id}",
                UserDTO.class, userId
        );
        ShareDTO shareDTO = new ShareDTO();
        // 消息装配
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());

        return shareDTO;
    }


}
