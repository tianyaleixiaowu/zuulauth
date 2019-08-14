package com.tianyalei.zuul.zuulauth.zuul;

import com.tianyalei.zuul.zuulauth.tool.FastJsonUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Set;

import static com.tianyalei.zuul.zuulauth.tool.Constant.ROLE_PERMISSION_MESSAGE_CHANNEL_NAME;
import static com.tianyalei.zuul.zuulauth.tool.Constant.USER_ROLE_MESSAGE_CHANNEL_NAME;
import static com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder.ROLE_PERMISSION_HASH_KEY;
import static com.tianyalei.zuul.zuulauth.zuul.AuthInfoHolder.USER_ROLE_HASH_KEY;

/**
 * @author wuweifeng wrote on 2019-08-13.
 */
public class AuthCache {
    private RedisTemplate<String, String> redisTemplate;
    
    public AuthCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;    
    }
    
    public void saveRolePermission(String roleKey, Set<String> set) {
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                redisTemplate.opsForHash().put(ROLE_PERMISSION_HASH_KEY, roleKey, FastJsonUtils.convertObjectToJSON(set));
                redisTemplate.convertAndSend(ROLE_PERMISSION_MESSAGE_CHANNEL_NAME, roleKey);
                return null;
            }
        });
    }

    public void removeRolePermission(String roleKey) {
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                redisTemplate.opsForHash().delete(ROLE_PERMISSION_HASH_KEY, roleKey);
                redisTemplate.convertAndSend(ROLE_PERMISSION_MESSAGE_CHANNEL_NAME, roleKey);
                return null;
            }
        });
    }

    public void saveUserRole(String userKey, Set<String> set) {
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                redisTemplate.opsForHash().put(USER_ROLE_HASH_KEY, userKey, FastJsonUtils.convertObjectToJSON(set));
                redisTemplate.convertAndSend(USER_ROLE_MESSAGE_CHANNEL_NAME, userKey);
                return null;
            }
        });
        
    }
    
    public void removeUserRole(String userKey) {
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                redisTemplate.opsForHash().delete(USER_ROLE_HASH_KEY, userKey);
                redisTemplate.convertAndSend(USER_ROLE_MESSAGE_CHANNEL_NAME, userKey);
                return null;
            }
        });

    }

}
