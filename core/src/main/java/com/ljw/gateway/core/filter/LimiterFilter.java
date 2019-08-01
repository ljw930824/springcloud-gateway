package com.ljw.gateway.core.filter;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName: AuthFilter
 * @Description: TODO
 * @Author: ljw
 * @Date: 2019/7/26 14:07
 **/
@Component
@Order(-2)
public class LimiterFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // check not really needed, since it is guarded in setStatusCode,
            // but it's a good example
            if (!exchange.getResponse().isCommitted()) {
                System.out.println(response.getStatusCode());
                System.out.println(response.getHeaders());
                if ("429".equals(exchange.getResponse().getStatusCode())) {
                    JSONObject message = new JSONObject();
                    message.put("status", -100);
                    message.put("data", "当前请求速率过快");
                    byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = response.bufferFactory().wrap(bits);
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    //指定编码，否则在浏览器中会中文乱码
                    response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                    response.writeWith(Mono.just(buffer));
                }
            }
        }));
    }

}
