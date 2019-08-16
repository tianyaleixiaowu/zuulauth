package com.tianyalei.zuul.zuulauth.config;

import com.tianyalei.zuul.zuulauth.cache.AuthCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author wuweifeng wrote on 2019-08-14.
 */
@Configuration
@ConditionalOnMissingBean({ZuulAuthConfigure.class, ClientAuthCacheConfigure.class}) //不是zuul工程时，才启用该配置
public class ClientAuthCacheConfigure {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Bean
    @ConditionalOnMissingBean
    AuthCache authCache() {
        return new AuthCache(redisTemplate);
    }
}
