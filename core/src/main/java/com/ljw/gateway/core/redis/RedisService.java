package com.ljw.gateway.core.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RedisService
 * @Description: redisService
 * @Author: ljw
 * @Date: 2019/7/30 14:21
 **/
@Slf4j
@Component
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private HashOperations<String, String, Object> hashOperations;
    @Autowired
    private ValueOperations<String, Object> valueOperations;

    public List<String> execCmd(String cmd, List<String> args) {
        return redisTemplate.execute(new RedisCallback<List<String>>() {

            @Override
            public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
                List<String> returnValue = new ArrayList<>();
                byte[][] argsArray = null;
                if (args != null && args.size() > 0) {
                    argsArray = new byte[args.size()][];
                    for (int i = 0; i < args.size(); i++) {
                        argsArray[i] = args.get(i).getBytes();
                    }
                }
                Object result = connection.execute(cmd, argsArray);
                if (result instanceof List) {
                    for (Object r : (List) result) {
                        returnValue.add(new String((byte[]) r));
                    }
                } else if (result instanceof byte[]) {
                    returnValue.add(new String((byte[]) result));
                }
                return returnValue;
            }
        });
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public void hashPut(String key, Map<String, Object> mapValue, int timeout, TimeUnit unit) {
        try {
            hashOperations.putAll(key, mapValue);
            expire(key, timeout, unit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void hashPut(String key, Map<String, Object> mapValue) {
        try {
            hashOperations.putAll(key, mapValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void hashPut(String key, String hashKey, Object hashValue) {
        try {
            hashOperations.put(key, hashKey, hashValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Map<String, Object> entries(String key) {
        return hashOperations.entries(key);
    }

    public void valueSet(String key, Object value) {
        valueOperations.set(key, value);
    }

    public void valueSet(String key, Object value, int timeout, TimeUnit unit) {
        valueOperations.set(key, value);
        expire(key, timeout, unit);
    }

    public void valueSetIfAbsent(String key, Object value, int timeout, TimeUnit unit) {
        valueOperations.setIfAbsent(key, value);
        expire(key, timeout, unit);
    }

    public Object valueGet(String key) {
        try {
            return valueOperations.get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String valueGetString(String key) {
        return (String) valueGet(key);
    }

    public void expire(String key, int timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    public Object hashGet(String key, String hashKey) {
        return hashOperations.get(key, hashKey);
    }

    public String hashGetString(String key, String hashKey) {
        return (String) hashGet(key, hashKey);
    }

    /**
     * 判断键是否存在
     *
     * @param key
     * @return
     */
    public boolean exsits(String key) {
        return redisTemplate.hasKey(key);
    }

    public boolean notExsits(String key) {
        return !exsits(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public long increment(String key) {
        return increment(key, 1);
    }

    public long increment(String key, long step) {
        return valueOperations.increment(key, step);
    }

    public boolean hashExists(String key, String hashKey) {
        return hashOperations.hasKey(key, hashKey);
    }

    public Long hashDelete(String key, String hashKey) {
        return hashOperations.delete(key, hashKey);
    }
}

