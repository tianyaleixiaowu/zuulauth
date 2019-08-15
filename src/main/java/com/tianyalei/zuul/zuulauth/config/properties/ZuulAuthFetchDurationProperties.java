package com.tianyalei.zuul.zuulauth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author wuweifeng wrote on 2019-08-15.
 */
@ConfigurationProperties(prefix = "zuulauth.duration")
public class ZuulAuthFetchDurationProperties {
    /**
     * 默认5分钟拉取一次各微服务的mapping信息。-1为只初始化一次，之后不拉取
     */
    private int mappingFetch = 5;
    /**
     * 拉取一次全量的user-role信息 。-1为只初始化一次，之后不拉取
     */
    private int roleFetch = 30;
    /**
     * 拉取一次全量的role-code信息 。-1为只初始化一次，之后不拉取
     */
    private int codeFetch = 30;
    /**
     * 启动后延迟多久开始拉取一次
     */
    private int delay = 5;

    private TimeUnit timeUnit = TimeUnit.MINUTES;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getMappingFetch() {
        return mappingFetch;
    }

    public void setMappingFetch(int mappingFetch) {
        this.mappingFetch = mappingFetch;
    }

    public int getRoleFetch() {
        return roleFetch;
    }

    public void setRoleFetch(int roleFetch) {
        this.roleFetch = roleFetch;
    }

    public int getCodeFetch() {
        return codeFetch;
    }

    public void setCodeFetch(int codeFetch) {
        this.codeFetch = codeFetch;
    }
}
