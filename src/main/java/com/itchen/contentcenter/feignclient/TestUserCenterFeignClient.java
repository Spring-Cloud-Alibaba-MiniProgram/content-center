package com.itchen.contentcenter.feignclient;

import com.itchen.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 测试多参数请求构造 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-07
 */
@FeignClient(name = "user-center")
public interface TestUserCenterFeignClient {

    @GetMapping("/q")
    UserDTO query(@SpringQueryMap UserDTO userDTO);

}
