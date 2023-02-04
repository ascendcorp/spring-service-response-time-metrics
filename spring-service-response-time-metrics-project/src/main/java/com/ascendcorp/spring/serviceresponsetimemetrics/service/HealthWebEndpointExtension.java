package com.ascendcorp.spring.serviceresponsetimemetrics.service;

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

    private static final String PREFIX = "health";
    private static final String UP = "UP";
    private static final String DOWN = "DOWN";
    private static final String BODY = "body";
    private static final String STATUS = "status";
    private static final String DETAIL = "detail";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String name;

    private boolean aggregated;

    private String url;

    @Autowired(required = false)
    private StringRedisTemplate redis;

    @Autowired(required = false)
    private HealthNotification healthNotification;

    private int interval = 0;
    private Health health;
    private long lastUpdate;

    @Override
    public Health health() {
        if(withinInterval()) {
            return health;
        }

        ResponseEntity<Map> response = to(url);

        String status = DOWN;

        Map detail = new HashMap<>();
        detail.put(BODY, null);
        boolean up = false;
        if (Objects.nonNull(response) && response.getStatusCode().is2xxSuccessful()) {
            detail.put(BODY, response.getBody());
            up = true;
            status = UP;
        }

        detail.put("url", url);
        if(up) {
            health = Health.up()
                    .withDetail(STATUS, status)
                    .withDetail(DETAIL, detail)
                    .build();
        } else {
            if(!aggregated) {
                health = Health.unknown()
                        .withDetail("status", status)
                        .withDetail("detail", detail)
                        .build();
            } else {
                health = Health.down()
                        .withDetail("status", status)
                        .withDetail("detail", detail)
                        .build();
            }

        }

        try {
            validateCacheChanged(status);
        } catch (Exception e) {
            logger.warn("" + e.getMessage());
        }
        return health;
    }

    private boolean withinInterval() {
        if((System.currentTimeMillis() - lastUpdate) < interval) {
            return true;
        } else {
            lastUpdate = System.currentTimeMillis();
            return false;
        }
    }

    private void validateCacheChanged(String status) {
        Object cached = getRedisCacheData(PREFIX, name);
        boolean update = false;
        if(cached == null) {
            update = true;
        } else if (!status.equalsIgnoreCase(cached.toString())) {
            onCacheChanged(status);
            update = true;
        }

        if(update) {
            putRedisCacheData(PREFIX, name, status);
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