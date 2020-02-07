package com.itchen.contentcenter.service.content;

import com.itchen.contentcenter.dao.share.ShareMapper;
import com.itchen.contentcenter.domain.dto.content.ShareDTO;
import com.itchen.contentcenter.domain.dto.user.UserDTO;
import com.itchen.contentcenter.domain.entity.share.Share;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

        // 1. 代码不可读
        // 2. 复制 URL 难以维护，https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&tn=02003390_42_hao_pg&wd=%E8%A7%86%E9%A2%91&oq=aaa&rsv_pq=c27d61f2000068e5&rsv_t=ab73spP6qpdFF7go6UQnqMRcbceWjDqmAFkKY0FXaFe8%2FH7BIuVjMx3TUrZLDvmSCmU%2BFWt54U0a&rqlang=cn&rsv_enter=0&rsv_dl=tb&inputT=731&rsv_sug3=8&rsv_sug1=1&rsv_sug7=100&rsv_sug2=0&rsv_sug4=731
        // 3. 难以响应需求的变化，变化很没有幸福感
        // 4. 编程体验不统一
        UserDTO userDTO = restTemplate.getForObject(
                "http://user-center/users/{userId}",
                UserDTO.class, userId
        );
        ShareDTO shareDTO = new ShareDTO();
        // 消息装配
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());

        return shareDTO;
    }


}
