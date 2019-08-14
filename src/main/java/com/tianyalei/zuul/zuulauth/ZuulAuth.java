package com.tianyalei.zuul.zuulauth;

import com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * zuul启动后初始化拉取所有服务的接口信息入内存
 * @author wuweifeng wrote on 2019/8/13.
 */
public class ZuulAuth {
    private RedisTemplate<String, String> redisTemplate;

    public ZuulAuth(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void init() {
        AuthInfoHolder.saveAllMappingInfo(redisTemplate);
    }
}
