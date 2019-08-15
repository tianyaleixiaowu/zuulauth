package com.tianyalei.zuul.zuulauth.zuul;

import com.tianyalei.zuul.zuulauth.bean.MethodAuthBean;
import com.tianyalei.zuul.zuulauth.tool.FastJsonUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存各客户端的地址映射信息、userRole信息、roleCode信息
 *
 * @author wuweifeng wrote on 2019/8/12.
 */
public class AuthInfoHolder {
    public static final String CLIENT_REQUEST_MAPPING_HASH_KEY = "client_request_mapping_hash_key";
    static final String USER_ROLE_HASH_KEY = "user_role_hash_key";
    static final String ROLE_PERMISSION_HASH_KEY = "role__permission_hash_key";
    /**
     * key是服务的名字，value是该服务的各接口权限集合
     */
    private static final ConcurrentHashMap<String, List<MethodAuthBean>> CLIENT_REQUEST_MAPPING_MAP =
            new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Set<String>> USER_ROLE_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Set<String>> ROLE_PERMISSION_MAP =
            new ConcurrentHashMap<>();

    public static Set<String> keys() {
        return CLIENT_REQUEST_MAPPING_MAP.keySet();
    }

    /**
     * 这一步是zuul端调用的，根据请求的服务名获取app的所有映射信息
     */
    public static List<MethodAuthBean> findByAppName(String appName) {
        //  [{
        //        "actions": [
        //            "POST"
        //        ],
        //        "codes": [],
        //        "codesLogical": null,
        //        "roles": [
        //            "typeSys"
        //        ],
        //        "rolesLogical": "AND",
        //        "urls": [
        //            "/menu"
        //        ]
        //    }
        //    ]
        return CLIENT_REQUEST_MAPPING_MAP.get(appName);
    }

    public static void saveAllMappingInfo(RedisTemplate<String, String> redisTemplate) {
        Set<Object> strings = redisTemplate.opsForHash().keys(CLIENT_REQUEST_MAPPING_HASH_KEY);
        for (Object o : strings) {
            String appName = o.toString();
            saveMappingInfo(appName, redisTemplate);
        }
    }

    /**
     * 将redis里的mapping拉入本地内存
     */
    public static void saveMappingInfo(String appName, RedisTemplate<String, String> redisTemplate) {
        String authStr = (String) redisTemplate.opsForHash().get(CLIENT_REQUEST_MAPPING_HASH_KEY, appName);
        List<MethodAuthBean> list = FastJsonUtils.toList(authStr, MethodAuthBean.class);

        CLIENT_REQUEST_MAPPING_MAP.put(appName, list);
    }

    /**
     * 将redis里的user-role拉入本地内存
     */
    public static void saveUserRoleInfo(String userKey, RedisTemplate<String, String> redisTemplate) {
        String userRoles = (String) redisTemplate.opsForHash().get(USER_ROLE_HASH_KEY, userKey);
        //说明user被删
        if (userRoles == null) {
            USER_ROLE_MAP.remove(userKey);
            return;
        }
        Set list = FastJsonUtils.toBean(userRoles, Set.class);

        USER_ROLE_MAP.put(userKey, list);
    }

    public static void saveRolePermissionInfo(String roleKey, RedisTemplate<String, String> redisTemplate) {
        String rolePermi = (String) redisTemplate.opsForHash().get(ROLE_PERMISSION_HASH_KEY, roleKey);
        //说明role被删
        if (rolePermi == null) {
            ROLE_PERMISSION_MAP.remove(roleKey);
            return;
        }
        Set list = FastJsonUtils.toBean(rolePermi, Set.class);

        ROLE_PERMISSION_MAP.put(roleKey, list);
    }

    public static Set<String> findByRole(String roleKey) {
        return ROLE_PERMISSION_MAP.get(roleKey);
    }

    public static Set<String> findByUser(String userKey) {
        return USER_ROLE_MAP.get(userKey);
    }
}
