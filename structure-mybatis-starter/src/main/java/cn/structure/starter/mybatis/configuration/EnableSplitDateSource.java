package cn.structure.starter.mybatis.configuration;

import cn.structure.starter.mybatis.plugin.SplitDateSourcePlugin;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Sq
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/26 23:47
 */
@Configuration
@ConditionalOnProperty(value = "structure.mybatis.plugin.split", havingValue = "true")
public class EnableSplitDateSource {

    @Bean
    @ConditionalOnMissingBean(SplitDateSourcePlugin.class)
    public Interceptor splitDateSourcePlugin() {
        return new SplitDateSourcePlugin();
    }

}
