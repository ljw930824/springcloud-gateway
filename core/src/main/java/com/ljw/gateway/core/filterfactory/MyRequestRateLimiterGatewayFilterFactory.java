package com.ljw.gateway.core.filterfactory;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.HttpStatusHolder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setResponseStatus;

/**
 * @ClassName: MyRequestRateLimiterGatewayFilterFactory
 * @Description: TODO
 * @Author: ljw
 * @Date: 2019/7/26 16:19
 **/
@Component
public class MyRequestRateLimiterGatewayFilterFactory extends
        AbstractGatewayFilterFactory<MyRequestRateLimiterGatewayFilterFactory.Config> {
    /**
     * Key-Resolver key.
     */
    public static final String KEY_RESOLVER_KEY = "keyResolver";

    private static final String EMPTY_KEY = "____EMPTY_KEY__";

    private final RateLimiter defaultRateLimiter;

    private final KeyResolver defaultKeyResolver;

    /**
     * Switch to deny requests if the Key Resolver returns an empty key, defaults to true.
     */
    private boolean denyEmptyKey = true;

    /**
     * HttpStatus to return when denyEmptyKey is true, defaults to FORBIDDEN.
     */
    private String emptyKeyStatusCode = HttpStatus.FORBIDDEN.name();

    public MyRequestRateLimiterGatewayFilterFactory(RateLimiter defaultRateLimiter,
                                                    KeyResolver defaultKeyResolver) {
        super(Config.class);
        this.defaultRateLimiter = defaultRateLimiter;
        this.defaultKeyResolver = defaultKeyResolver;
    }

    public KeyResolver getDefaultKeyResolver() {
        return defaultKeyResolver;
    }

    public RateLimiter getDefaultRateLimiter() {
        return defaultRateLimiter;
    }

    public boolean isDenyEmptyKey() {
        return denyEmptyKey;
    }

    public void setDenyEmptyKey(boolean denyEmptyKey) {
        this.denyEmptyKey = denyEmptyKey;
    }

    public String getEmptyKeyStatusCode() {
        return emptyKeyStatusCode;
    }

    public void setEmptyKeyStatusCode(String emptyKeyStatusCode) {
        this.emptyKeyStatusCode = emptyKeyStatusCode;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GatewayFilter apply(Config config) {
        KeyResolver resolver = getOrDefault(config.keyResolver, defaultKeyResolver);
        RateLimiter<Object> limiter = getOrDefault(config.rateLimiter,
                defaultRateLimiter);
        boolean denyEmpty = getOrDefault(config.denyEmptyKey, this.denyEmptyKey);
        HttpStatusHolder emptyKeyStatus = HttpStatusHolder
                .parse(getOrDefault(config.emptyKeyStatus, this.emptyKeyStatusCode));

        return (exchange, chain) -> {
            Route route = exchange
                    .getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

            return resolver.resolve(exchange).defaultIfEmpty(EMPTY_KEY).flatMap(key -> {
                if (EMPTY_KEY.equals(key)) {
                    if (denyEmpty) {
                        setResponseStatus(exchange, emptyKeyStatus);
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange);
                }
                return limiter.isAllowed(route.getId(), key).flatMap(response -> {

                    for (Map.Entry<String, String> header : response.getHeaders()
                            .entrySet()) {
                        exchange.getResponse().getHeaders().add(header.getKey(),
                                header.getValue());
                    }

                    if (response.isAllowed()) {
                        return chain.filter(exchange);
                    }

                    /**
                     * 其他代码和源代码一样,只是这里将返回值改为JSON格式
                     */
                    JSONObject result = new JSONObject();
                    result.put("status", "fail");
                    result.put("msg", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
                    byte[] bytes = result.toJSONString().getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    ServerHttpResponse rs = exchange.getResponse();
                    rs.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    rs.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                    return rs.writeWith(Mono.just(buffer));
                });
            });
        };
    }

    private <T> T getOrDefault(T configValue, T defaultValue) {
        return (configValue != null) ? configValue : defaultValue;
    }

    public static class Config {

        private KeyResolver keyResolver;

        private RateLimiter rateLimiter;

        private HttpStatus statusCode = HttpStatus.TOO_MANY_REQUESTS;

        private Boolean denyEmptyKey;

        private String emptyKeyStatus;

        public KeyResolver getKeyResolver() {
            return keyResolver;
        }

        public Config setKeyResolver(KeyResolver keyResolver) {
            this.keyResolver = keyResolver;
            return this;
        }

        public RateLimiter getRateLimiter() {
            return rateLimiter;
        }

        public Config setRateLimiter(RateLimiter rateLimiter) {
            this.rateLimiter = rateLimiter;
            return this;
        }

        public HttpStatus getStatusCode() {
            return statusCode;
        }

        public Config setStatusCode(HttpStatus statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Boolean getDenyEmptyKey() {
            return denyEmptyKey;
        }

        public Config setDenyEmptyKey(Boolean denyEmptyKey) {
            this.denyEmptyKey = denyEmptyKey;
            return this;
        }

        public String getEmptyKeyStatus() {
            return emptyKeyStatus;
        }

        public Config setEmptyKeyStatus(String emptyKeyStatus) {
            this.emptyKeyStatus = emptyKeyStatus;
            return this;
        }

    }
}
