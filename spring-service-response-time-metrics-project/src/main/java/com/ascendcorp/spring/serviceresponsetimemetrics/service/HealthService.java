package com.ascendcorp.spring.serviceresponsetimemetrics.service;

import lombok.Data;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Configuration
@ConfigurationProperties(prefix = "third-party-health-checker")
public class HealthService {

    private List<Endpoint> endpoints;

    private boolean mainStatusConsidered;

    @Bean
    public Optional add(DefaultListableBeanFactory context) {
        System.out.println("Add health check endpoints");
        AtomicInteger i = new AtomicInteger();
        endpoints.forEach((n) -> {
            GenericBeanDefinition gbd = new GenericBeanDefinition();
            gbd.setBeanClass(HealthWebEndpointExtension.class);

            String bean = "" + n.name;
            gbd.getPropertyValues().addPropertyValue("name", bean);
            gbd.getPropertyValues().addPropertyValue("url", n.url);
            gbd.getPropertyValues().addPropertyValue("considered", mainStatusConsidered);

            context.registerBeanDefinition(bean, gbd);
        });

        return Optional.empty();
    }

    @Data
    public static class Endpoint {

        private String name;
        private String url;
    }
}
