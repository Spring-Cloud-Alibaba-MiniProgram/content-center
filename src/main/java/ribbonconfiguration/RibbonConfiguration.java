package ribbonconfiguration;

import com.itchen.contentcenter.configuration.NacosSameClusterWeightedRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ribbonconfiguration 包必须在启动类包外;是因为父子上下文的原因 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-06
 */
@Configuration
public class RibbonConfiguration {

    @Bean
    public IRule ribbonRule() {
        return new NacosSameClusterWeightedRule();
    }

}
