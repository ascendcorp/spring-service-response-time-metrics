package com.ascendcorp.spring.serviceresponsetimemetrics.dto;

import com.ascendcorp.spring.serviceresponsetimemetrics.configuration.ServiceRequestInterceptor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Configuration
@ConfigurationProperties(prefix = "service-response-time")
public class ServiceResponseTimeConfig {

    /**
     * Grouped url in case of any part variable url and prefer a single metric for those endpoint
     */
    private List<String> groupedUrls;

    private boolean enabled = true;

    @Bean
    public Optional addGlobalInterceptor(ApplicationContext ctx) {

        if(enabled) {
            System.out.println("Enabled service-response-time");
            System.out.println("Add global interceptor " + this.groupedUrls);

            Map<String, RestTemplate> allRestTemplate = ctx.getBeansOfType(RestTemplate.class);

            for (Map.Entry<String, RestTemplate> entry : allRestTemplate.entrySet()) {
                setClientRequestInterceptorToInterceptor(entry.getValue());
            }
        } else {
            System.out.println("Disabled service-response-time");
        }
        return Optional.empty();
    }

    private void setClientRequestInterceptorToInterceptor(RestTemplate restTemplate) {
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new ServiceRequestInterceptor(this.groupedUrls));
    }
}
