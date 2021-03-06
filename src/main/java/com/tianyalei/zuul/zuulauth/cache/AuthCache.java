package com.tianyalei.zuul.zuulauth.cache;

import com.tianyalei.zuul.zuulauth.tool.FastJsonUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

import static com.tianyalei.zuul.zuulauth.tool.Constant.ROLE_PERMISSION_MESSAGE_CHANNEL_NAME;
import static com.tianyalei.zuul.zuulauth.tool.Constant.USER_ROLE_MESSAGE_CHANNEL_NAME;
import static com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder.ROLE_PERMISSION_HASH_KEY;
import static com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder.USER_ROLE_HASH_KEY;

/**
 * 微服务client端调用该方法后，会将信息写入redis，并会通知zuul，zuul会将信息拉取到zuul的内存.
 * 自己也可以使用该类，做对应的缓存录入和查询
 * @author wuweifeng wrote on 2019-08-13.
 */
public class AuthCache {
    private StringRedisTemplate stringRedisTemplate;
    
    public AuthCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    
    public void saveRolePermission(String roleKey, Set<String> set) {
        stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                stringRedisTemplate.opsForHash().put(ROLE_PERMISSION_HASH_KEY, roleKey, FastJsonUtils.convertObjectToJSON(set));
                stringRedisTemplate.convertAndSend(ROLE_PERMISSION_MESSAGE_CHANNEL_NAME, roleKey);
                return null;
            }
        });
    }

    public void removeRolePermission(String roleKey) {
        stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                stringRedisTemplate.opsForHash().delete(ROLE_PERMISSION_HASH_KEY, roleKey);
                stringRedisTemplate.convertAndSend(ROLE_PERMISSION_MESSAGE_CHANNEL_NAME, roleKey);
                return null;
            }
        });
    }

    public void saveUserRole(String userKey, Set<String> set) {
        stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                stringRedisTemplate.opsForHash().put(USER_ROLE_HASH_KEY, userKey, FastJsonUtils.convertObjectToJSON(set));
                stringRedisTemplate.convertAndSend(USER_ROLE_MESSAGE_CHANNEL_NAME, userKey);
                return null;
            }
        });
        
    }
    
    public void removeUserRole(String userKey) {
        stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                stringRedisTemplate.opsForHash().delete(USER_ROLE_HASH_KEY, userKey);
                stringRedisTemplate.convertAndSend(USER_ROLE_MESSAGE_CHANNEL_NAME, userKey);
                return null;
            }
        });

    }

    public Set<String> findByUser(String userKey) {
        Object userRoles = stringRedisTemplate.opsForHash().get(USER_ROLE_HASH_KEY, userKey);
        //说明user被删
        if (userRoles == null) {
            return null;
        }
        return FastJsonUtils.toBean(userRoles.toString(), Set.class);
    }

    public Set<String> findByRole(String roleKey) {
        Object rolePermi = stringRedisTemplate.opsForHash().get(ROLE_PERMISSION_HASH_KEY, roleKey);
        //说明role被删
        if (rolePermi == null) {
            return null;
        }
        return FastJsonUtils.toBean(rolePermi.toString(), Set.class);
    }
}
