package com.tianyalei.zuul.zuulauth.config;

import com.tianyalei.zuul.zuulauth.config.properties.ZuulAuthFetchDurationProperties;
import com.tianyalei.zuul.zuulauth.zuul.AuthChecker;
import com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.ZuulProxyMarkerConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author wuweifeng wrote on 2019-08-13.
 */
@Configuration
@EnableConfigurationProperties(ZuulAuthFetchDurationProperties.class)
@ConditionalOnBean(ZuulProxyMarkerConfiguration.class) //这一句是当前工程是zuul工程时，才启用该configuration
public class ZuulAuthConfigure {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ZuulAuthFetchDurationProperties properties;

    @Resource
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    AuthInfoHolder authInfoHolder() {
        return new AuthInfoHolder(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    AuthChecker authChecker() {
        return new AuthChecker(applicationContext);
    }

    /**
     * 启动后自动获取redis里的权限信息到内存
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        logger.info("开始拉取所有客户端mapping信息");
        //拉取mapping信息
        AuthInfoHolder authInfoHolder = authInfoHolder();
        authInfoHolder.saveAllMappingInfo();

        logger.info(authInfoHolder.keys().toString());
        logger.info("拉取完毕");

        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);
        if (properties.getMappingFetch() > 0) {
            scheduledExecutor.scheduleAtFixedRate(authInfoHolder::saveAllMappingInfo,
                    properties.getDelay(), properties.getMappingFetch(), properties.getTimeUnit());
        }
        if (properties.getRoleFetch() > 0) {
            scheduledExecutor.scheduleAtFixedRate(authInfoHolder::saveAllUserRole,
                    properties.getDelay(), properties.getMappingFetch(), properties.getTimeUnit());
        }
        if (properties.getCodeFetch() > 0) {
            scheduledExecutor.scheduleAtFixedRate(authInfoHolder::saveAllRoleCode,
                    properties.getDelay(), properties.getMappingFetch(), properties.getTimeUnit());
        }

    }

}
