package com.tianyalei.zuul.zuulauth.zuul.inter;

import java.util.Set;

/**
 * @author wuweifeng wrote on 2019-08-19.
 */
public interface IpRuleChecker {
    /**
     * 供用户实现规则，譬如从redis中获取白名单库，来比对userIp在不在里面。如果在黑\白名单，则返回true
     */
    boolean check(String userIp);

    /**
     * 默认应该是ip白名单所有的APP都通过，黑名单都拒绝，但也可以排除几个APP
     */
    Set<String> exceptApps();
}
