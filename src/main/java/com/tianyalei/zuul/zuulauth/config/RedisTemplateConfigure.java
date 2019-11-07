package com.tianyalei.zuul.zuulauth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author wuweifeng wrote on 2017/10/27.
 */
@Configuration
@ConditionalOnMissingBean(StringRedisTemplate.class)
public class RedisTemplateConfigure {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Bean(name = "stringRedisTemplate")
    @Primary
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        logger.info("redisTemplate初始化完毕");
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }


}
