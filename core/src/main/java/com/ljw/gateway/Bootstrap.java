package com.ljw.gateway;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ljw.gateway.core.ratelimiterkeyresolver.HostAddrKeyResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
@EnableApolloConfig
@EnableCircuitBreaker
public class Bootstrap {

    @RequestMapping(value = "/fallback")
    @ResponseStatus
    public Mono<Map<String, Object>> fallback(ServerWebExchange exchange, Throwable throwable) {
        Map<String, Object> result = new HashMap<>(2);
        ServerHttpRequest request = exchange.getRequest();
//        result.put("path", request.getPath().pathWithinApplication().value());
//        result.put("method", request.getMethodValue());
//        if (null != throwable.getCause()) {
//            result.put("message", throwable.getCause().getMessage());
//        } else {
//            result.put("message", throwable.getMessage());
//        }
        result.put("code", "-100");
        result.put("data", "service not available");
        return Mono.just(result);
    }


    @RequestMapping("/timeout")
    public Mono<Map<String, Object>> timeout(ServerWebExchange exchange, Throwable throwable) throws InterruptedException {
        Map<String, Object> result = new HashMap<>(2);
        ServerHttpRequest request = exchange.getRequest();
        result.put("code", "-100");
        result.put("data", "service not available");
        Thread.sleep(10000L);
        return Mono.just(result);
    }

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

//    @Bean
//    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(p -> p
//                        .path("/get")
//                        .filters(f -> f.addRequestHeader("Hello", "World"))
//                        .uri("http://httpbin.org:80"))
//                .build();
//    }

//    @Bean
//    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(p -> p
//                        .path("/get")
//                        .filters(f -> f.addRequestHeader("Hello", "World"))
//                        .uri("http://httpbin.org:80"))
//                .route(p -> p
//                        .host("*.hystrix.com")
//                        .filters(f -> f.hystrix(config -> config.setName("mycmd")))
//                        .uri("http://httpbin.org:80")).
//                        build();
//    }

//    @Bean
//    public UriKeyResolver uriKeyResolver() {
//        return new UriKeyResolver();
//    }

    @Bean
    public HostAddrKeyResolver hostAddrKeyResolver() {
        return new HostAddrKeyResolver();
    }
}
