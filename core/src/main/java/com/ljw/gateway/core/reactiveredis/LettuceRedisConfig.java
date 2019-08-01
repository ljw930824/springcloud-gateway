package com.ljw.gateway.core.reactiveredis;//package com.example.demo.reactiveredis;
//
//import com.example.demo.serializer.KryoRedisSerializer;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
//import org.springframework.data.redis.core.ReactiveRedisTemplate;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.time.Duration;
//
///**
// * @ClassName: RedisConfig
// * @Description: TODO
// * @Author: ljw
// * @Date: 2019/7/31 16:32
// **/
//@Configuration
//public class LettuceRedisConfig {
//
//    @Value("${spring.redis.database}")
//    private int database;
//
//    @Value("${spring.redis.host}")
//    private String host;
//
//    @Value("${spring.redis.password:#{null}}")
//    private String password;
//
//    @Value("${spring.redis.port}")
//    private int port;
//
//    @Value("${spring.redis.timeout}")
//    private long timeout;
//
//    @Value("${spring.redis.lettuce.pool.max-idle}")
//    private int maxIdle;
//
//    @Value("${spring.redis.lettuce.pool.min-idle}")
//    private int minIdle;
//
//    @Value("${spring.redis.lettuce.pool.max-active}")
//    private int maxActive;
//
//    @Value("${spring.redis.lettuce.pool.max-wait}")
//    private long maxWait;
//
//    /**
//     * 缓存有效期
//     */
//    private Duration entryTtl = Duration.ofHours(24L);
//
//    /**
//     * 连接超时
//     */
//    private Duration connectTimeout = Duration.ofSeconds(15L);
//
//    /**
//     * 读取超时
//     */
//    private Duration readTimeout = Duration.ofSeconds(30L);
//
//    @Bean
//    LettuceConnectionFactory lettuceConnectionFactory(GenericObjectPoolConfig genericObjectPoolConfig) {
//        // 单机版配置
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setDatabase(database);
//        redisStandaloneConfiguration.setHostName(host);
//        redisStandaloneConfiguration.setPort(port);
//        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
//
//        // 集群版配置
////        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
////        String[] serverArray = clusterNodes.split(",");
////        Set<RedisNode> nodes = new HashSet<RedisNode>();
////        for (String ipPort : serverArray) {
////            String[] ipAndPort = ipPort.split(":");
////            nodes.add(new RedisNode(ipAndPort[0].trim(), Integer.valueOf(ipAndPort[1])));
////        }
////        redisClusterConfiguration.setPassword(RedisPassword.of(password));
////        redisClusterConfiguration.setClusterNodes(nodes);
////        redisClusterConfiguration.setMaxRedirects(maxRedirects);
//
//        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
//                .commandTimeout(Duration.ofMillis(timeout))
//                .poolConfig(genericObjectPoolConfig)
//                .build();
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
//        return factory;
//    }
//
//    /**
//     * GenericObjectPoolConfig 连接池配置
//     *
//     * @return
//     */
//    @Bean
//    public GenericObjectPoolConfig genericObjectPoolConfig() {
//        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
//        genericObjectPoolConfig.setMaxIdle(maxIdle);
//        genericObjectPoolConfig.setMinIdle(minIdle);
//        genericObjectPoolConfig.setMaxTotal(maxActive);
//        genericObjectPoolConfig.setMaxWaitMillis(maxWait);
//        return genericObjectPoolConfig;
//    }
//
//    /**
//     * redisTemplate 序列化使用的jdkSerializeable, 存储二进制字节码, 所以自定义序列化类
//     *
//     * @param redisConnectionFactory
//     * @return
//     */
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
//        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        KryoRedisSerializer<Object> kryoRedisSerializer = new KryoRedisSerializer<>(Object.class);
//        template.setKeySerializer(stringRedisSerializer);
//        template.setValueSerializer(kryoRedisSerializer);
//        template.setHashKeySerializer(stringRedisSerializer);
//        template.setHashValueSerializer(kryoRedisSerializer);
//        template.afterPropertiesSet();
//        return template;
//    }
//}
