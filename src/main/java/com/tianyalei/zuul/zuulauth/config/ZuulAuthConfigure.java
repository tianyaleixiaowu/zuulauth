package com.tianyalei.zuul.zuulauth.config;

import com.tianyalei.zuul.zuulauth.ZuulAuth;
import com.tianyalei.zuul.zuulauth.zuul.AuthChecker;
import com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.zuul.ZuulProxyMarkerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author wuweifeng wrote on 2019-08-13.
 */
@Configuration
@ConditionalOnMissingBean(ZuulAuthConfigure.class)
@ConditionalOnBean(ZuulProxyMarkerConfiguration.class) //这一句是当前工程是zuul工程时，才启用该configuration
public class ZuulAuthConfigure {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Bean
    @ConditionalOnMissingBean
    ZuulAuth zuulAuth() {
        return new ZuulAuth(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    AuthChecker authChecker() {
        return new AuthChecker();
    }

    /**
     * 启动后自动获取redis里的权限信息到内存
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        logger.info("开始拉取所有客户端mapping信息");
        //拉取mapping信息
        zuulAuth().init();
        logger.info(AuthInfoHolder.keys().toString());
        logger.info("拉取完毕");
    }
}
