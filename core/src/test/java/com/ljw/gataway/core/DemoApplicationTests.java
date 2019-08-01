package com.ljw.gataway.core;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ljw.gateway.core.Bootstrap;
import com.ljw.gateway.core.redis.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Bootstrap.class)
public class DemoApplicationTests {

    @Autowired
    private RedisService redisService;

    @Test
    public void test() throws Exception {
        // 保存字符串
        redisService.valueSet("aaa", "111");
        redisService.valueSet("aaa", "2222");
        redisService.valueSet("ccc", "exper", 3600, TimeUnit.SECONDS);
        Map<String, Object> temp = new HashMap<>();
        temp.put("okay", "ljw123");
        temp.put("fine", "1234567");
        redisService.hashPut("test", temp);

//        Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
    }

    @Test
    public void testGetRedisList() {
        System.out.println(redisService.valueGet("blacklist_ip"));
    }

    @Test
    public void testApollo() {
        Config config = ConfigService.getAppConfig(); //config instance is singleton for each namespace and is never null
        String someKey = "test";
        String someDefaultValue = "someDefaultValueForTheKey";
        String value = config.getProperty(someKey, someDefaultValue);
        String[] values = config.getArrayProperty("test1", ",", new String[]{});
        System.out.println(value);
        for (String temp :
                values) {
            System.out.println(temp);
        }

    }

}
