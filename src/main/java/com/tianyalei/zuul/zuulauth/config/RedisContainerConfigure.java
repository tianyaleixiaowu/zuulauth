package com.tianyalei.zuul.zuulauth.config;

import com.tianyalei.zuul.zuulauth.tool.Constant;
import com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder;
import com.tianyalei.zuul.zuulauth.zuul.listener.ChannelMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.zuul.ZuulProxyMarkerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import javax.annotation.Resource;

/**
 * @author wuweifeng wrote on 2019/8/12.
 */
@Configuration
@ConditionalOnMissingBean(RedisMessageListenerContainer.class)
@ConditionalOnBean(ZuulProxyMarkerConfiguration.class) //这一句是当前工程是zuul工程时，才启用该configuration
public class RedisContainerConfigure {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private AuthInfoHolder authInfoHolder;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter clientRequestMappingListenerAdapter,
                                            MessageListenerAdapter userRoleListenerAdapter,
                                            MessageListenerAdapter rolePermissionListenerAdapter) {
        logger.info("初始化redis的监听器，开始监听channel");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //订阅某个频道
        container.addMessageListener(clientRequestMappingListenerAdapter, new PatternTopic(Constant.CLIENT_REQUEST_MAPPING_CHANNEL_NAME));
        container.addMessageListener(userRoleListenerAdapter, new PatternTopic(Constant.USER_ROLE_MESSAGE_CHANNEL_NAME));
        container.addMessageListener(rolePermissionListenerAdapter, new PatternTopic(Constant.ROLE_PERMISSION_MESSAGE_CHANNEL_NAME));

        return container;
    }


    /**
     * 表示监听一个频道
     */
    @Bean("clientRequestMappingListenerAdapter")
    MessageListenerAdapter clientRequestMappingListenerAdapter() {
        ChannelMessageListener channelMessageListener = new ChannelMessageListener(authInfoHolder);
        //这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“MessageReceiveTwo ”
        return new MessageListenerAdapter(channelMessageListener, "listenMappingEvent");
    }

    @Bean("userRoleListenerAdapter")
    MessageListenerAdapter userRoleListenerAdapter() {
        ChannelMessageListener channelMessageListener = new ChannelMessageListener(authInfoHolder);
        //这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“MessageReceiveTwo ”
        return new MessageListenerAdapter(channelMessageListener, "listenUserRoleEvent");
    }

    @Bean("rolePermissionListenerAdapter")
    MessageListenerAdapter rolePermissionListenerAdapter() {
        ChannelMessageListener channelMessageListener = new ChannelMessageListener(authInfoHolder);
        //这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“MessageReceiveTwo ”
        return new MessageListenerAdapter(channelMessageListener, "listenRolePermissionEvent");
    }
}
