package com.tianyalei.zuul.zuulauth.zuul.listener;

import com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订阅的频道(客户端mapping变化)发生变化会调用该方法
 * @author wuweifeng wrote on 2019/8/12.
 */
public class ChannelMessageListener {
    public ChannelMessageListener(AuthInfoHolder authInfoHolder) {
        this.authInfoHolder = authInfoHolder;
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AuthInfoHolder authInfoHolder;

    public void listenMappingEvent(String appName) {
        logger.info("收到映射(新建/变更)消息，来自于：<" + appName + ">服务");

        authInfoHolder.saveMappingInfo(appName);

        logger.info("<" + appName + ">服务的权限变更完毕");
    }

    public void listenUserRoleEvent(String userKey) {
        logger.info("收到user-role(新建/变更)消息，userKey：<" + userKey + ">");

        authInfoHolder.saveUserRoleInfo(userKey);

        logger.info("<" + userKey + ">服务的权限变更完毕");
    }

    public void listenRolePermissionEvent(String roleKey) {
        logger.info("收到role-permission(新建/变更)消息，roleKey：<" + roleKey + ">");

        authInfoHolder.saveRolePermissionInfo(roleKey);

        logger.info("<" + roleKey + ">服务的权限变更完毕");
    }
}
