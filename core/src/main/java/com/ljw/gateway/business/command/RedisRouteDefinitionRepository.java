package com.ljw.gateway.business.command;

import com.alibaba.fastjson.JSON;
import com.ljw.gateway.common.constants.RedisKeyConsts;
import com.ljw.gateway.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: RedisRouteDefinitionRepository
 * @Description: TODO
 * @Author: ljw
 * @Date: 2019/8/8 16:46
 **/
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {

    @Autowired
    private RedisService redisService;

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        redisService.entries(RedisKeyConsts.DYNAMIC_ROUTE_KEY).forEach((key, routeDefinition) -> {
            routeDefinitions.add(JSON.parseObject(routeDefinition.toString(), RouteDefinition.class));
        });
        return Flux.fromIterable(routeDefinitions);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }
}
