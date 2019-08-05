package com.ljw.gateway.business.apollo;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ljw.gateway.business.apollo.template.AbstractTemplateService;
import com.ljw.gateway.common.constants.ApolloConsts;
import com.ljw.gateway.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.stereotype.Component;

import static com.ljw.gateway.common.constants.RedisKeyConsts.BLACKLIST_IP_KEY;


/**
 * @ClassName: ApolloBlackWhiteListChanged
 * @Description: 监听Apollo配置中心的改变，实时往redis缓存中存入
 * <br/>（后期应该增加个手动刷新的接口，以保证同步实时性的稳定）
 * @Author: ljw
 * @Date: 2019/7/30 17:27
 **/
@Component
@Slf4j
public class ApolloIpBlackListChanged extends AbstractTemplateService {

    @Autowired
    private RedisService redisService;

    @Override
    @ApolloConfigChangeListener("TEST1.123")
    public void doCheck(ConfigChangeEvent changeEvent) {
        for (String key : changeEvent.changedKeys()) {
            ConfigChange change = changeEvent.getChange(key);
            log.info("Found change TEST1.123 - {}", change.toString());
        }
        this.doChangeHandler(changeEvent);
        // 更新相应的bean的属性值，主要是存在@ConfigurationProperties注解的bean
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
    }

    @Override
    public void doChangeHandler(ConfigChangeEvent changeEvent) {
        /** 生产环境尽量避免keys操作，很容易造成缓存穿透 **/
//        Set<String> keys = redisTemplate.keys(BLACKLIST_IP_KEY + StringConsts.COLON + "*");
        redisService.delete(BLACKLIST_IP_KEY);
        log.info("ApolloBlackWhiteListChanged ------ all blackList delete in Redis");
        for (String key : changeEvent.changedKeys()) {
            ConfigChange change = changeEvent.getChange(key);
            if (ApolloConsts.BLACKLIST.equals(change.getPropertyName())) {
                String newValue = change.getNewValue();
                redisService.valueSet(BLACKLIST_IP_KEY, newValue);
                log.info("blackList into Redis  - {}", newValue);
            }
        }
    }
}
