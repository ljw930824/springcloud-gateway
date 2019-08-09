package com.ljw.gateway.core.ratelimiterkeyresolver;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @ClassName: UriKeyResolver
 * @Description: url key
 * @Author: ljw
 * @Date: 2019/7/26 10:00
 **/
public class UriKeyResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getURI().getPath());
    }
}



