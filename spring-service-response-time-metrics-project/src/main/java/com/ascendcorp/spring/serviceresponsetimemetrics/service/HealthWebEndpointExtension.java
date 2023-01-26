package com.ascendcorp.spring.serviceresponsetimemetrics.service;

import io.lettuce.core.RedisConnectionException;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class HealthWebEndpointExtension implements HealthIndicator {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String name;

    private boolean considered;

    private String url;

    @Autowired(required = false)
    private StringRedisTemplate redis;

    @Autowired(required = false)
    private HealthNotification healthNotification;

    @Override
    public Health health() {
        ResponseEntity<Map> response = to(url);

        String reason = "Server does not respond";
        String status = "DOWN";

        Map detail = new HashMap<>();
        detail.put("body", null);
        boolean up = false;
        if (Objects.nonNull(response) && response.getStatusCode().is2xxSuccessful()) {
            reason = "Server is up";
            detail.put("body", response.getBody());
            up = true;
            status = "UP";
        }

        if(!considered) {
            up = true;
        }

        detail.put("url", url);
        Health health;
        if(up) {
            health = Health.up()
                    .withDetail("reason", reason)
                    .withDetail("detail", detail)
                    .build();
        } else {
            health = Health.down()
                    .withDetail("reason", reason)
                    .withDetail("detail", detail)
                    .build();
        }

        try {
            validateCacheChanged(status);
        } catch (Exception e) {
            logger.warn("" + e.getMessage());
        }
        return health;
    }

    private void validateCacheChanged(String status) {
        Object cached = getRedisCacheData("system", name);
        boolean update = false;
        if(cached == null) {
            update = true;
        } else if (!status.equalsIgnoreCase(cached.toString())) {
            onCacheChanged(status);
            update = true;
        }

        if(update) {
            putRedisCacheData("system", name, status);
        }
    }

    private Object getRedisCacheData(String prefix, String key) {
        return redis.opsForValue().get(prefix + "_" + key);
    }

    private void putRedisCacheData(String prefix, String key, String data) {
        redis.opsForValue().set(prefix + "_" + key, data);
    }

    private void onCacheChanged(String status) {
        if(healthNotification != null) {
            healthNotification.push("[" + status + "] " + url);
        } else {
            logger.warn("HealthNotification not found.");
        }
    }

    public static ResponseEntity<Map> to(String url) {
        try {
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

            WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();

             return webClient.get().uri(url)
                     .retrieve()
                     .toEntity(Map.class).block();
        } catch (Exception e) {
            System.err.println("fail to request by WebClient to url: " + url + e.getMessage());
        }
        return null;
    }
}