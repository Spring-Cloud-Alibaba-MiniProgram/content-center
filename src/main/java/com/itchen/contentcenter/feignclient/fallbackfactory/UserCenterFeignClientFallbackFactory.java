package com.itchen.contentcenter.feignclient.fallbackfactory;

import com.itchen.contentcenter.domain.dto.user.UserDTO;
import com.itchen.contentcenter.feignclient.UserCenterFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign 整合 Sentinel 后 @FeignClient(name = "user-center",fallbackFactory = Xxx.class) .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-08
 */
@Slf4j
@Component
public class UserCenterFeignClientFallbackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable throwable) {
        return new UserCenterFeignClient() {
            @Override
            public UserDTO findById(Integer id) {
                log.warn("远程调用被限流/降级了", throwable);
                UserDTO userDTO = new UserDTO();
                userDTO.setWxNickname("一个默认用户");
                return userDTO;
            }
        };
    }
}
