package com.itchen.contentcenter.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import ribbonconfiguration.RibbonConfiguration;

/**
 * 用户中心负载均衡配置，Java 代码配置方式
 * 全局配置：RibbonClients .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-06
 */
@Configuration
// @RibbonClient(name = "user-center", configuration = RibbonConfiguration.class)
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class UserCenterRibbonConfiguration {

}
