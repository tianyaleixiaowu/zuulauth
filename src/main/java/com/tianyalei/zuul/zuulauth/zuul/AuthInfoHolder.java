package com.tianyalei.zuul.zuulauth.zuul;

import com.tianyalei.zuul.zuulauth.bean.MethodAuthBean;
import com.tianyalei.zuul.zuulauth.tool.FastJsonUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存各客户端的地址映射信息、userRole信息、roleCode信息
 *
 * @author wuweifeng wrote on 2019/8/12.
 */
public class AuthInfoHolder {
    public static final String CLIENT_REQUEST_MAPPING_HASH_KEY = "client_request_mapping_hash_key";
    static final String USER_ROLE_HASH_KEY = "user_role_hash_key";
    static final String ROLE_PERMISSION_HASH_KEY = "role_permission_hash_key";
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

    private static List<String> multiGet(String key, List<Object> keyList,
                                         RedisTemplate<String, String> redisTemplate) {
        //这一步通过pipeLine获取所有的values，会被封装为一个List<List<Object>>对象返回，内层的list就是对应的各个value
        List<Object> objects = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                redisTemplate.opsForHash().multiGet(key, keyList);
                return null;
            }
        });
        return (List<String>) objects.get(0);
    }

    public static void saveAllMappingInfo(RedisTemplate<String, String> redisTemplate) {
        //所有的key
        Set<Object> strings = redisTemplate.opsForHash().keys(CLIENT_REQUEST_MAPPING_HASH_KEY);
        if (strings.size() == 0) {
            return;
        }
        List<Object> keyList = new ArrayList<>(strings);
        //从redis拉回所有的value
        List<String> valueList = multiGet(CLIENT_REQUEST_MAPPING_HASH_KEY, keyList, redisTemplate);

        CLIENT_REQUEST_MAPPING_MAP.clear();
        for (int i = 0; i < keyList.size(); i++) {
            List<MethodAuthBean> list = FastJsonUtils.toList(valueList.get(i), MethodAuthBean.class);
            CLIENT_REQUEST_MAPPING_MAP.put(keyList.get(0).toString(), list);
        }
    }

    public static void saveAllUserRole(RedisTemplate<String, String> redisTemplate) {
        Set<Object> strings = redisTemplate.opsForHash().keys(USER_ROLE_HASH_KEY);
        if (strings.size() == 0) {
            return;
        }
        List<Object> keyList = new ArrayList<>(strings);
        List<String> valueList = multiGet(USER_ROLE_HASH_KEY, keyList, redisTemplate);

        USER_ROLE_MAP.clear();
        for (int i = 0; i < keyList.size(); i++) {
            Set set = FastJsonUtils.toBean(valueList.get(i), Set.class);
            USER_ROLE_MAP.put(keyList.get(0).toString(), set);
        }
    }

    public static void saveAllRoleCode(RedisTemplate<String, String> redisTemplate) {
        Set<Object> strings = redisTemplate.opsForHash().keys(ROLE_PERMISSION_HASH_KEY);
        if (strings.size() == 0) {
            return;
        }
        List<Object> keyList = new ArrayList<>(strings);
        List<String> valueList = multiGet(ROLE_PERMISSION_HASH_KEY, keyList, redisTemplate);

        ROLE_PERMISSION_MAP.clear();
        for (int i = 0; i < keyList.size(); i++) {
            Set set = FastJsonUtils.toBean(valueList.get(i), Set.class);
            ROLE_PERMISSION_MAP.put(keyList.get(0).toString(), set);
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
        Set set = FastJsonUtils.toBean(userRoles, Set.class);

        USER_ROLE_MAP.put(userKey, set);
    }

    public static void saveRolePermissionInfo(String roleKey, RedisTemplate<String, String> redisTemplate) {
        String rolePermi = (String) redisTemplate.opsForHash().get(ROLE_PERMISSION_HASH_KEY, roleKey);
        //说明role被删
        if (rolePermi == null) {
            ROLE_PERMISSION_MAP.remove(roleKey);
            return;
        }
        Set set = FastJsonUtils.toBean(rolePermi, Set.class);

        ROLE_PERMISSION_MAP.put(roleKey, set);
    }

    public static Set<String> findByRole(String roleKey) {
        return ROLE_PERMISSION_MAP.get(roleKey);
    }

    public static Set<String> findByUser(String userKey) {
        return USER_ROLE_MAP.get(userKey);
    }
}
