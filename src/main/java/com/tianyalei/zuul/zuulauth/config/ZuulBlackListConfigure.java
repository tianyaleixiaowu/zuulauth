package com.tianyalei.zuul.zuulauth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.zuul.ZuulProxyMarkerConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuweifeng wrote on 2019-08-19.
 */
@Configuration
@ConditionalOnMissingBean(ZuulBlackListConfigure.class)
@ConditionalOnBean(ZuulProxyMarkerConfiguration.class)
public class ZuulBlackListConfigure {

}
