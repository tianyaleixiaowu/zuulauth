package com.tianyalei.zuul.zuulauth.config;

import com.tianyalei.zuul.zuulauth.ClientAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.annotation.Resource;

/**
 * 客户端配置，在客户端启动后自动上传所有的接口mapping配置
 * @author wuweifeng wrote on 2019/8/12.
 */
@Configuration
@ConditionalOnMissingBean({ZuulAuthConfigure.class, ClientRequestMappingConfigure.class}) //不是zuul工程时，才启用该配置
public class ClientRequestMappingConfigure {
    @Resource
    private ApplicationContext applicationContext;

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    @ConditionalOnMissingBean
    ClientAuth clientAuth() {
        return new ClientAuth(applicationContext);
    }

    /**
     * 启动后自动上报权限信息到redis
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        //上报mapping信息
        clientAuth().init(appName);
    }
}
