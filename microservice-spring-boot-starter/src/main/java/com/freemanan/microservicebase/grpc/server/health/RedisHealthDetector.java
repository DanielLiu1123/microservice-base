package com.freemanan.microservicebase.grpc.server.health;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.Assert;

/**
 * Detects the health of redis.
 *
 * @author Freeman
 * @since 2022/8/17
 */
public class RedisHealthDetector implements HealthDetector {

    private static final Logger log = LoggerFactory.getLogger(RedisHealthDetector.class);

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisHealthDetector(RedisConnectionFactory redisConnectionFactory) {
        Assert.notNull(redisConnectionFactory, "redisConnectionFactory can't be null");
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public ServingStatus check(HealthCheckRequest request) {
        try {
            redisConnectionFactory.getConnection().ping();
        } catch (Exception e) {
            log.warn("Redis health check failed!", e);
            return ServingStatus.NOT_SERVING;
        }
        return ServingStatus.SERVING;
    }
}
