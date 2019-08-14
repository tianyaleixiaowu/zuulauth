package com.tianyalei.zuul.zuulauth;

import com.tianyalei.zuul.zuulauth.annotation.RequireCodes;
import com.tianyalei.zuul.zuulauth.annotation.RequireRoles;
import com.tianyalei.zuul.zuulauth.bean.MethodAuthBean;
import com.tianyalei.zuul.zuulauth.tool.FastJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tianyalei.zuul.zuulauth.tool.Constant.CLIENT_REQUEST_MAPPING_CHANNEL_NAME;
import static com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder.CLIENT_REQUEST_MAPPING_HASH_KEY;

/**
 * 启动后上报自己的所有接口权限
 *
 * @author wuweifeng wrote on 2019/8/9.
 */
public class ClientAuth {
    private ApplicationContext applicationContext;

    public ClientAuth(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void init(String appName) {
        logger.info("开始获取并上传本应用接口信息、权限信息");
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        List<MethodAuthBean> list = new ArrayList<>();
        //获取到所有的方法映射
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            MethodAuthBean methodAuthBean = new MethodAuthBean();

            RequestMappingInfo info = m.getKey();
            HandlerMethod handlerMethod = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();

            //一个接口可能存在多个匹配的url
            Set<String> urls = p.getPatterns();
            for (String url : urls) {
                //如 /role/{id}/abc/{a}
                //替换为/role/[1-9][0-9]*/abc/[1-9][0-9]*   将来好做正则匹配
                if (url.contains("{") && url.contains("}")) {
                    url = url.replaceAll("\\{[^}]*\\}", "[1-9][0-9]*");
                }
                methodAuthBean.getUrls().add(url);
            }                                     
            //methodAuthBean.setUrls(urls);

            //获取它的类的注解
            Class c = handlerMethod.getMethod().getDeclaringClass();
            //如果该类上标注了"需要role权限"，那么该类下的所有接口，都默认需要该role权限，除非被方法上的覆盖
            if (c.isAnnotationPresent(RequireRoles.class)) {
                RequireRoles role = (RequireRoles) c.getAnnotation(RequireRoles.class);
                String[] needRole = role.value();
                CollectionUtils.mergeArrayIntoCollection(needRole, methodAuthBean.getRoles());
                methodAuthBean.setRolesLogical(role.logical());
            }
            //如果类上标注了需要某些权限，那么该类下的所有接口，都默认需要该权限，除非被方法上的覆盖
            if (c.isAnnotationPresent(RequireCodes.class)) {
                RequireCodes codes = (RequireCodes) c.getAnnotation(RequireCodes.class);
                String[] needCodes = codes.value();
                CollectionUtils.mergeArrayIntoCollection(needCodes, methodAuthBean.getCodes());
                methodAuthBean.setCodesLogical(codes.logical());
            }

            //取到具体的接口方法，方法上的会覆盖类上的
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(RequireRoles.class)) {
                RequireRoles role = (RequireRoles) c.getAnnotation(RequireRoles.class);
                String[] needRole = role.value();
                if (needRole.length > 0) {
                    //清除默认继承来自类上的角色
                    methodAuthBean.getRoles().clear();
                }
                CollectionUtils.mergeArrayIntoCollection(needRole, methodAuthBean.getRoles());
                methodAuthBean.setRolesLogical(role.logical());
            }
            if (method.isAnnotationPresent(RequireCodes.class)) {
                RequireCodes codes = (RequireCodes) c.getAnnotation(RequireCodes.class);
                String[] needCodes = codes.value();
                if (needCodes.length > 0) {
                    //清除默认继承来自类上的权限信息
                    methodAuthBean.getCodes().clear();
                }
                CollectionUtils.mergeArrayIntoCollection(needCodes, methodAuthBean.getCodes());
                methodAuthBean.setCodesLogical(codes.logical());
            }

            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            //取Get，Post .如果是RequestMapping的话，则是空
            Set<String> actions =
                    methodsCondition.getMethods().stream().map(RequestMethod::toString).collect(Collectors.toSet());
            if (actions.size() == 0) {
                actions.add("*");
            }
            methodAuthBean.setActions(actions);

            list.add(methodAuthBean);
        }

        RedisTemplate<String, String> redisTemplate = applicationContext.getBean(RedisTemplate.class);
        if (list.size() > 0) {
            //存入redis
            redisTemplate.opsForHash().put(CLIENT_REQUEST_MAPPING_HASH_KEY, appName, FastJsonUtils.convertObjectToJSON(list));
            //发布消息通知zuul，该prjKey的信息有变，需重新拉取
            redisTemplate.convertAndSend(CLIENT_REQUEST_MAPPING_CHANNEL_NAME, appName);
        }
    }

}
