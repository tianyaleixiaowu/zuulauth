package com.tianyalei.zuul.zuulauth.zuul.listener;

import com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 订阅的频道(客户端mapping变化)发生变化会调用该方法
 * @author wuweifeng wrote on 2019/8/12.
 */
public class ChannelMessageListener {
    public ChannelMessageListener(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RedisTemplate<String, String> redisTemplate;

    public void listenMappingEvent(String appName) {
        logger.info("收到映射(新建/变更)消息，来自于：<" + appName + ">服务");

        AuthInfoHolder.saveMappingInfo(appName, redisTemplate);

        logger.info("<" + appName + ">服务的权限变更完毕");
    }

    public void listenUserRoleEvent(String userKey) {
        logger.info("收到user-role(新建/变更)消息，userKey：<" + userKey + ">");

        AuthInfoHolder.saveUserRoleInfo(userKey, redisTemplate);

        logger.info("<" + userKey + ">服务的权限变更完毕");
    }

    public void listenRolePermissionEvent(String roleKey) {
        logger.info("收到role-permission(新建/变更)消息，roleKey：<" + roleKey + ">");

        AuthInfoHolder.saveRolePermissionInfo(roleKey, redisTemplate);

        logger.info("<" + roleKey + ">服务的权限变更完毕");
    }
}
