package com.ljw.gateway.core.filter.blackwhitelist;

import com.google.common.collect.Maps;
import com.ljw.gateway.core.blackwhitelist.BlackWhiteListFactory;
import com.ljw.gateway.core.blackwhitelist.BlackWhiteListType;
import com.ljw.gateway.core.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ipresolver.RemoteAddressResolver;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Map;

import static com.ljw.gateway.core.common.constants.RedisKeyConsts.BLACKLIST_IP_KEY;


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
            String blackIps = redisService.valueGetString(BLACKLIST_IP_KEY);
            Map<BlackWhiteListType, String> blackIpMap = Maps.newHashMap();
            blackIpMap.put(BlackWhiteListType.BLACKLIST, blackIps);
            blackWhiteListFactory.setBlackWhiteIPs(blackIpMap);
            if (blackWhiteListFactory.check(BlackWhiteListType.BLACKLIST, clientIp)) {
                log.info("intercept invalid request from forbidden ip {}", clientIp);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return Mono.empty();
            }
        } catch (Exception e) {
            log.error("IpBlackListFilter error", e);
        }
        return chain.filter(exchange);
    }
}
