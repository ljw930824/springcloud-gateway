package com.ljw.gateway.common.constants;

/**
 * @ClassName: RedisKeyConsts
 * @Description: redisKey常量
 * @Author: ljw
 * @Date: 2019/7/30 14:21
 **/
public class RedisKeyConsts {

    private RedisKeyConsts() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BLACKLIST_IP_KEY = "blacklist_ip";
    public static final String BLACKLIST_IP_KEY_OLD = "blacklist_ip_old";
    public static final String DYNAMIC_ROUTE_KEY = "dynamic_route";
    public static final String DYNAMIC_ROUTE_KEY_OLD = "dynamic_route_old";
    public static final String WHITELIST_API_KEY = "whitelist_api";
    public static final String WHITELIST_SERVICE_KEY = "whitelist_service";
    public static final String WHITELIST_API_PATTERN_KEY = "whitelist_api_pattern";
}
