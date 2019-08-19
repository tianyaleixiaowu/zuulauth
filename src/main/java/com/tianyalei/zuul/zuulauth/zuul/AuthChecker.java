package com.tianyalei.zuul.zuulauth.zuul;

import com.tianyalei.zuul.zuulauth.annotation.Logical;
import com.tianyalei.zuul.zuulauth.bean.MethodAuthBean;
import com.tianyalei.zuul.zuulauth.tool.RouteLocatorUtil;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 判断权限是否通过
 *
 * @author wuweifeng wrote on 2019/8/12.
 */
public class AuthChecker {
    @Resource
    private ApplicationContext applicationContext;

    public AuthChecker(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static final int CODE_OK = 0;
    /**
     * 不存在该服务的mapping信息
     */
    public static final int CODE_NO_APP = -1;
    /**
     * 不存在的路径
     */
    public static final int CODE_404 = -2;
    /**
     * role不符
     */
    public static final int CODE_NO_ROLE = -3;
    /**
     * code不符
     */
    public static final int CODE_NO_CODE = -4;
    /*
    //  [{
    //        //        "actions": [
    //        //            "POST"
    //        //        ],
    //        //        "codes": [],
    //        //        "codesLogical": null,
    //        //        "roles": [
    //        //            "typeSys"
    //        //        ],
    //        //        "rolesLogical": "AND",
    //        //        "urls": [
    //        //            "/menu/add","/menu/sub"
    //        //        ]
    //        //    }
    //        //    ]
    */

    public int check(HttpServletRequest serverHttpRequest, String userRole, Set<String> codes) {
        Set<String> set = new HashSet<>();
        set.add(userRole);
        return check(serverHttpRequest, set, codes);
    }

    public int check(HttpServletRequest serverHttpRequest, Set<String> userRoles, Set<String>
            userCodes) {
        //类似于  /zuuldmp/core/test
        String requestPath = serverHttpRequest.getRequestURI();
        //解析出该请求，是请求的哪个后端服务（根据zuul的路由规则获取）
        String[] array = RouteLocatorUtil.parseAppNameAndPath(applicationContext.getBean(RouteLocator.class),
                requestPath);
        if (array[0] == null) {
            return CODE_NO_APP;
        }
        String path = array[1];

        //所有的映射信息
        List<MethodAuthBean> list = applicationContext.getBean(AuthInfoHolder.class).findByAppName(array[0]);
        if (list == null) {
            return CODE_NO_APP;
        }
        String method = serverHttpRequest.getMethod().toUpperCase();
        //判断action和path的映射
        MethodAuthBean methodAuthBean = checkPathAndAction(path, method, list);
        if (methodAuthBean == null) {
            return CODE_404;
        }
        //判断role
        if (!checkRoleAndCode(methodAuthBean.getRoles(), methodAuthBean.getRolesLogical(), userRoles)) {
            return CODE_NO_ROLE;
        }
        //判断code
        if (!checkRoleAndCode(methodAuthBean.getCodes(), methodAuthBean.getCodesLogical(), userCodes)) {
            return CODE_NO_CODE;
        }

        return CODE_OK;
    }

    /**
     * 找到匹配的path、method
     */
    private MethodAuthBean checkPathAndAction(String path, String method, List<MethodAuthBean> list) {
        for (MethodAuthBean methodAuthBean : list) {
            //判断url是否有匹配的
            for (String url : methodAuthBean.getUrls()) {
                boolean isMatch = Pattern.matches(url, path);
                if (isMatch) {
                    //判断method，为*的通配
                    Set<String> allowMethod = methodAuthBean.getActions();
                    if (allowMethod.contains("*") || allowMethod.contains(method)) {
                        return methodAuthBean;
                    }
                }

            }

        }
        return null;
    }

    private boolean checkRoleAndCode(Set<String> requireSet, Logical logical, Set<String> userSet) {
        //如果该方法不需要权限，直接算通过
        if (CollectionUtils.isEmpty(requireSet)) {
            return true;
        }
        if (Logical.AND == logical) {
            return userSet.containsAll(requireSet);
        } else {
            //是or时，只要包含一个需要的role即可
            for (String requireRole : requireSet) {
                if (userSet.contains(requireRole)) {
                    return true;
                }
            }
        }

        return false;
    }

}
