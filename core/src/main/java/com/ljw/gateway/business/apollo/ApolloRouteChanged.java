package com.ljw.gateway.business.apollo;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.google.common.collect.Lists;
import com.ljw.gateway.business.apollo.template.AbstractTemplateService;
import com.ljw.gateway.common.constants.ApolloConsts;
import com.ljw.gateway.core.route.DynamicRouteResolver;
import com.ljw.gateway.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ljw.gateway.common.constants.RedisKeyConsts.DYNAMIC_ROUTE_KEY;
import static com.ljw.gateway.common.constants.RedisKeyConsts.DYNAMIC_ROUTE_KEY_OLD;


/**
 * @ClassName: ApolloBlackWhiteListChanged
 * @Description: 监听Apollo配置中心的改变，实时往redis缓存中存入
 * <br/>（后期应该增加个手动刷新的接口，以保证同步实时性的稳定）
 * @Author: ljw
 * @Date: 2019/7/30 17:27
 **/
@Component
@Slf4j
public class ApolloRouteChanged extends AbstractTemplateService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private DynamicRouteResolver dynamicRouteService;

    @Override
    @ApolloConfigChangeListener("TEST1.123")
    public void doCheck(ConfigChangeEvent changeEvent) {
        for (String key : changeEvent.changedKeys()) {
            ConfigChange change = changeEvent.getChange(key);
            if (change.getPropertyName().contains(ApolloConsts.DYNAMICROUTE)) {
                log.info("监听到配置变化" + change.getNamespace() + " -> " + change.getPropertyName() + " - {}", change.toString());
            }
        }
        this.doChangeHandler(changeEvent);
        // 更新相应的bean的属性值，主要是存在@ConfigurationProperties注解的bean
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
    }

    @Override
    public void doChangeHandler(ConfigChangeEvent changeEvent) {
        List<RouteDefinition> routeDefinitionList = Lists.newArrayList();
        for (String key : changeEvent.changedKeys()) {
            ConfigChange change = changeEvent.getChange(key);
            if (change.getPropertyName().contains(ApolloConsts.DYNAMICROUTE)) {
                String newValue = change.getNewValue();
                String oldValue = change.getOldValue();
                redisService.hashPut(DYNAMIC_ROUTE_KEY, change.getPropertyName(), newValue);
                redisService.hashPut(DYNAMIC_ROUTE_KEY_OLD, change.getPropertyName(), oldValue);
                log.info("从Apollo读取动态路由新的数据已录入Redis  - {}", newValue);
                log.info("从Apollo读取动态路由回滚数据已录入Redis  - {}", oldValue);
                routeDefinitionList.add(JSON.parseObject(newValue, RouteDefinition.class));
            }
        }
        dynamicRouteService.add(routeDefinitionList);
        log.info("当前动态路由设置信息已生效");
    }
}
