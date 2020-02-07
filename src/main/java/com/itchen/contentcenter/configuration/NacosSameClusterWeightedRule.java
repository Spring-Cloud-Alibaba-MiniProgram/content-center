package com.itchen.contentcenter.configuration;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 同一集群优先调用 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-06
 */
@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // 读取配置文件，并初始化 NacosWeightedRule
    }

    @Override
    public Server choose(Object key) {
        try {
            // 拿到配置文件中的集群名称 BJ
            String clusterName = nacosDiscoveryProperties.getClusterName();

            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            // 想要请求的微服务的名称
            String name = loadBalancer.getName();

            // 拿到服务发现的相关 API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            // 1. 找到指定服务的所有实例 A
            List<Instance> instances = namingService.selectInstances(name, true);

            // 2. 过滤出相同集群下的所有实例 B
            List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());

            // 3. 如果 B 是空，就用 A
            List<Instance> instancesToBeChosen = new ArrayList<>();
            if (CollectionUtils.isEmpty(sameClusterInstances)) {
                instancesToBeChosen = instances;
                log.warn("发生跨集群的调用，name = {}，clusterName = {}", name, clusterName);
            } else {
                instancesToBeChosen = sameClusterInstances;
            }

            // 4. 基于权重的负载均衡算法，返回 1 个实例
            Instance instance = ExtendBalancer.getHostByRandomWeight2(instancesToBeChosen);

            log.info("选择的实例是：port = {}，instance = {}", instance.getPort(), instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("发生异常了", e);
            return null;
        }
    }
}

/**
 * 当想使用别人代码时，而别人代码因为访问级别问题访问不上时，可以继承别人的类
 */
class ExtendBalancer extends Balancer {
    public static Instance getHostByRandomWeight2(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}
