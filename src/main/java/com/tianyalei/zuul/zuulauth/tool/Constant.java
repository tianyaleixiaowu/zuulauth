package com.tianyalei.zuul.zuulauth.tool;

/**
 * @author wuweifeng wrote on 2019/8/12.
 */
public interface Constant {

    /**
     * 客户端mapping权限变更信息
     */
    String CLIENT_REQUEST_MAPPING_CHANNEL_NAME = "client_request_mapping_channel_name";
    /**
     * 客户端user-role变更信息
     */
    String USER_ROLE_MESSAGE_CHANNEL_NAME = "user_role_message_channel_name";
    /**
     * 客户端role-permission变更信息
     */
    String ROLE_PERMISSION_MESSAGE_CHANNEL_NAME = "role_permission_message_channel_name";
}
