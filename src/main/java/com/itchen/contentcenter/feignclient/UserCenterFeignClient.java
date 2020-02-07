package com.itchen.contentcenter.feignclient;

import com.itchen.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户中心远程调用客户端
 * Java 代码方式配置日志级别(细粒度)，@FeignClient(name = "user-center", configuration = UserCenterFeignConfiguration.class).
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-07
 */
// @FeignClient(name = "user-center", configuration = UserCenterFeignConfiguration.class)
@FeignClient(name = "user-center")
public interface UserCenterFeignClient {

    /**
     * 根据用户 ID 获取用户信息
     * 当调用该方法时，会构建此 URL：http://user-center/users/{id}
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);

}
