package com.ljw.gateway.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.ljw.gateway.business.blackwhitelist.BlackWhiteListFactory;
import com.ljw.gateway.business.blackwhitelist.BlackWhiteListType;
import com.ljw.gateway.common.constants.RedisKeyConsts;
import com.ljw.gateway.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ipresolver.RemoteAddressResolver;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * @ClassName: IpBlackListFilter
 * @Description: ip黑名单过滤，主要通过redis和配置中心同步的数据为准
 * <br>
 * 支持IP段*通配符配置，如:<br>
 * 1.所有IP: *<br>
 * 2.所有192.168.开头的IP: 192.168.*<br>
 * 3.所有0网段的IP: 192.168.0.*<br>
 * 4.也支持这种格式的IP段指定: 10.195.13.0-10.195.13.255<br>
 * 5.单独IP指定: 192.168.1.100<br>
 * 6.所有以192.168.1开头.50结尾的IP，即192.168.10.50-192.168.19.50;
 * 192.168.100.50-192.168.199.50: 192.168.1*.50<br>
 * @Author: ljw
 * @Date: 2019/7/30 14:21
 **/
@Slf4j
@Component
@Order(-101)
public class IpBlackListFilter implements GlobalFilter {

    @Autowired
    private RedisService redisService;

    @Autowired
    private BlackWhiteListFactory blackWhiteListFactory;

    private final RemoteAddressResolver remoteAddressResolver = XForwardedRemoteAddressResolver
            .maxTrustedIndex(1);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            InetSocketAddress remoteAddress = remoteAddressResolver.resolve(exchange);
            String clientIp = remoteAddress.getHostName();
            String blackIps = redisService.valueGetString(RedisKeyConsts.BLACKLIST_IP_KEY);
            Map<BlackWhiteListType, String> blackIpMap = Maps.newConcurrentMap();
            blackIpMap.put(BlackWhiteListType.BLACKLIST, blackIps);
            blackWhiteListFactory.setBlackWhiteIPs(blackIpMap);
            if (blackWhiteListFactory.check(BlackWhiteListType.BLACKLIST, clientIp)) {
                log.info("intercept invalid request from forbidden ip {}", clientIp);
                ServerHttpResponse response = exchange.getResponse();
                JSONObject message = new JSONObject();
                message.put("status", 403);
                message.put("data", "当前请求处于黑名单，无法访问");
                byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bits);
                response.setStatusCode(HttpStatus.FORBIDDEN);
                //指定编码，否则在浏览器中会中文乱码
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                return response.writeWith(Mono.just(buffer));
            }
        } catch (Exception e) {
            log.error("IpBlackListFilter error", e);
        }
        return chain.filter(exchange);
    }
}
