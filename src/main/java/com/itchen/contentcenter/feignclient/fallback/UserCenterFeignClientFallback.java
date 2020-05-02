package com.itchen.contentcenter.feignclient.fallback;

import com.itchen.contentcenter.domain.dto.user.UserAddBonusDTO;
import com.itchen.contentcenter.domain.dto.user.UserDTO;
import com.itchen.contentcenter.feignclient.UserCenterFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.User;
import org.springframework.stereotype.Component;

/**
 * Feign 整合 Sentinel 后 @FeignClient(name = "user-center",fallback = Xxx.class) .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-08
 */
@Component
@Slf4j
public class UserCenterFeignClientFallback implements UserCenterFeignClient {
    @Override
    public UserDTO findById(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setWxNickname("一个默认用户");
        return userDTO;
    }

    @Override
    public User addBonus(UserAddBonusDTO dto) {
        log.warn("远程调用被限流/降级了");
        return null;
    }
}
