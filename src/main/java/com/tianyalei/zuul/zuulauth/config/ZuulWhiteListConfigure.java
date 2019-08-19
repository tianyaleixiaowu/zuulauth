package com.tianyalei.zuul.zuulauth.config;

import com.tianyalei.zuul.zuulauth.zuul.filter.WhileListFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.zuul.ZuulProxyMarkerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuweifeng wrote on 2019-08-19.
 */
@Configuration
@ConditionalOnMissingBean(ZuulWhiteListConfigure.class)
@ConditionalOnBean(ZuulProxyMarkerConfiguration.class)
public class ZuulWhiteListConfigure {

    @Bean
    public WhileListFilter whileListFilter() {
        return new WhileListFilter();
    }
    
}
