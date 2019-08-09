package com.ljw.gateway.business.command;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ljw.gateway.common.constants.RedisKeyConsts;
import com.ljw.gateway.core.route.DynamicRouteResolver;
import com.ljw.gateway.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName: RouteInitFromRedis
 * @Description: TODO
 * @Author: ljw
 * @Date: 2019/8/8 16:11
 **/
@Component
@Slf4j
public class RouteInitFromRedis implements CommandLineRunner {

    @Autowired
    private RedisService redisService;

    @Autowired
    private DynamicRouteResolver dynamicRouteService;

    @Override
    public void run(String... args) throws Exception {
        List<RouteDefinition> routeDefinitions = Lists.newArrayList();
        log.info("动态路由初始化开始---从Redis中取出路由，gateway刷新路由配置");
        redisService.entries(RedisKeyConsts.DYNAMIC_ROUTE_KEY).forEach((key, routeDefinition) -> {
            routeDefinitions.add(JSON.parseObject(routeDefinition.toString(), RouteDefinition.class));
        });
        dynamicRouteService.add(routeDefinitions);
        log.info("动态路由初始化结束");
    }
}
