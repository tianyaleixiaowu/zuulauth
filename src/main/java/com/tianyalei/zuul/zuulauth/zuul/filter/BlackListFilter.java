package com.tianyalei.zuul.zuulauth.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.tianyalei.zuul.zuulauth.config.ZuulBlackListConfigure;
import com.tianyalei.zuul.zuulauth.exception.IpRefuseException;
import com.tianyalei.zuul.zuulauth.tool.IpUtil;
import com.tianyalei.zuul.zuulauth.tool.RouteLocatorUtil;
import com.tianyalei.zuul.zuulauth.zuul.inter.IpRuleChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * ip黑名单过滤器。当开启了EnableBlackList时才启用
 *
 * @author wuweifeng wrote on 2019-08-19.
 */
@Component
@ConditionalOnBean(ZuulBlackListConfigure.class)
public class BlackListFilter extends ZuulFilter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IpRuleChecker ipRuleChecker;
    @Resource
    private RouteLocator routeLocator;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //让该filter位置靠前
        return -101;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws IpRefuseException {
        logger.info("进入ip黑名单过滤");

        if (ipRuleChecker == null) {
            logger.info("用户没有配置黑名单规则");
            return null;
        }

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest serverHttpRequest = ctx.getRequest();

        String userIp = IpUtil.getIpAddress(serverHttpRequest);
        logger.info("请求者ip是：" + userIp);
        boolean isBlack = ipRuleChecker.check(userIp);
        if (isBlack) {
            //解析出请求的app名字
            String appName = RouteLocatorUtil.parseAppNameAndPath(routeLocator, serverHttpRequest)[0];
            //这些被排除的APP，黑名单也允许访问
            Set<String> exceptApps = ipRuleChecker.exceptApps();
            if (!CollectionUtils.isEmpty(exceptApps) && exceptApps.contains(appName)) {
                logger.info("特例服务，已放行");
                return null;
            } else {
                throw new IpRefuseException();
            }
        }

        logger.info("已放行");
        return null;
    }
}
