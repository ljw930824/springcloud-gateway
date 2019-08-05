package com.ljw.gateway.reactiveredis;//package com.example.demo.reactiveredis;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.stereotype.Component;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisCluster;
//
//import java.util.Arrays;
//import java.util.Random;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.Condition;
//
///**
// * @ClassName: RedisLock
// * @Description: redis锁
// * @Author: ljw
// * @Date: 2019/7/30 14:21
// **/
//@Component
//@Slf4j
//public class RedisLock implements VariableKeyLock {
//
//    public static final String LOCK = "LOCK";
//
//    @Qualifier("lettuceConnectionFactory")
//    private LettuceConnectionFactory factory;
//
//    private ThreadLocal<String> localValue = new ThreadLocal<String>();
//
//    /**
//     * 解锁lua脚本
//     */
//    private static final String UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return 0 end";
//
//    @Override
//    public void lock() {
//        if (!tryLock()) {
//            try {
//                Thread.sleep(new Random().nextInt(10) + 1);
//            } catch (InterruptedException e) {
//                log.error(e.getMessage(), e);
//            }
//            lock();
//        }
//    }
//
//    @Override
//    public void lock(String key) {
//        if (!tryLock(key)) {
//            try {
//                Thread.sleep(new Random().nextInt(10) + 1);
//            } catch (InterruptedException e) {
//                log.error(e.getMessage(), e);
//            }
//            lock(key);
//        }
//    }
//
//    @Override
//    public boolean tryLock() {
//        RedisConnection connection = null;
//        try {
//            connection = factory.getConnection();
//            Jedis jedis = (Jedis) connection.getNativeConnection();
//            String value = UUID.randomUUID().toString();
//            localValue.set(value);
//            String ret = jedis.set(LOCK, value, "NX", "PX", 10000);
//            return ret != null && ret.equals("OK");
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            if (connection != null) {
//                connection.close();
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean tryLock(String key) {
//        RedisConnection connection = null;
//        try {
//            connection = factory.getConnection();
//            Jedis jedis = (Jedis) connection.getNativeConnection();
//            String value = UUID.randomUUID().toString();
//            localValue.set(value);
//            String ret = jedis.set(key, value, "NX", "PX", 10000);
//            return ret != null && ret.equals("OK");
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            if (connection != null) {
//                connection.close();
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void unlock() {
//        String script = UNLOCK_LUA;
//        RedisConnection connection = null;
//        try {
//            connection = factory.getConnection();
//            Object jedis = connection.getNativeConnection();
//            if (jedis instanceof Jedis) {
//                ((Jedis) jedis).eval(script, Arrays.asList(LOCK), Arrays.asList(localValue.get()));
//            } else if (jedis instanceof JedisCluster) {
//                ((JedisCluster) jedis).eval(script, Arrays.asList(LOCK), Arrays.asList(localValue.get()));
//            }
//
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            if (connection != null) {
//                connection.close();
//            }
//        }
//
//    }
//
//    @Override
//    public void unlock(String key) {
//        String script = UNLOCK_LUA;
//        RedisConnection connection = null;
//        try {
//            connection = factory.getConnection();
//            Object jedis = connection.getNativeConnection();
//            if (jedis instanceof Jedis) {
//                ((Jedis) jedis).eval(script, Arrays.asList(key), Arrays.asList(localValue.get()));
//            } else if (jedis instanceof JedisCluster) {
//                ((JedisCluster) jedis).eval(script, Arrays.asList(key), Arrays.asList(localValue.get()));
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            if (connection != null) {
//                connection.close();
//            }
//        }
//    }
//
//    // -------------------------------------
//
//    @Override
//    public void lockInterruptibly() throws InterruptedException {
//    }
//
//    @Override
//    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
//        return false;
//    }
//
//    @Override
//    public Condition newCondition() {
//        return null;
//    }
//
//}
