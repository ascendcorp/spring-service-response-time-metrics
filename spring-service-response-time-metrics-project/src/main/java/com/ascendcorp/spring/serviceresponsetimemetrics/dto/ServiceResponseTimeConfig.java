package com.ascendcorp.spring.serviceresponsetimemetrics.dto;

import com.ascendcorp.spring.serviceresponsetimemetrics.configuration.ServiceRequestInterceptor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Configuration
@ConfigurationProperties(prefix = "service-response-time")
public class ServiceResponseTimeConfig {

    private List<String> groupedUrls;

    @Bean
    public Optional addGlobalInterceptor(ApplicationContext ctx) {
        System.out.println("Add global interceptor " + this.groupedUrls);

        Map<String, RestTemplate> allRestTemplate = ctx.getBeansOfType(RestTemplate.class);

        for (Map.Entry<String, RestTemplate> entry : allRestTemplate.entrySet()) {
            setClientRequestInterceptorToInterceptor(entry.getValue());
        }

        return Optional.empty();
    }

    private void setClientRequestInterceptorToInterceptor(RestTemplate restTemplate) {
        List interceptors = restTemplate.getInterceptors();
        interceptors.add(new ServiceRequestInterceptor(this.groupedUrls));
    }
}
