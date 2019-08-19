package com.tianyalei.zuul.zuulauth.tool;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * zuul的路由规则工具类
 * @author wuweifeng wrote on 2019-08-19.
 */
public class RouteLocatorUtil {

    /**
     * 解析出该请求是请求的哪个微服务
     */
    public static String[] parseAppNameAndPath(RouteLocator routeLocator, HttpServletRequest serverHttpRequest) {
        return parseAppNameAndPath(routeLocator, serverHttpRequest.getRequestURI());
    }

    public static String[] parseAppNameAndPath(RouteLocator routeLocator, String requestPath) {
        String[] array = new String[2];
        //一个requestPath：//类似于  /zuuldmp/core/test。其中/zuuldmp是zuul的prefix，core是微服务的名字
        //获取所有路由信息，找到该请求对应的appName
        // 一个Route信息如：Route{id='one', fullPath='/zuuldmp/auth/**', path='/**', location='auth', prefix='/zuuldmp/auth',
        List<Route> routeList = routeLocator.getRoutes();
        String appName = null;
        String path = null;

        for (Route route : routeList) {
            if (requestPath.startsWith(route.getPrefix())) {
                //取到该请求对应的微服务名字
                appName = route.getLocation();
                //具体的微服务里面的请求路径，如 /test
                path = requestPath.replace(route.getPrefix(), "");
            }
        }
        array[0] = appName;
        array[1] = path;
        return array;
    }
}
