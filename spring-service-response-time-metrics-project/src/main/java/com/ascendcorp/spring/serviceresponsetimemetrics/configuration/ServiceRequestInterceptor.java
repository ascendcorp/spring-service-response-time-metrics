package com.ascendcorp.spring.serviceresponsetimemetrics.configuration;

import com.ascendcorp.spring.serviceresponsetimemetrics.dto.GroupedUrl;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;


/*

This class purpose for measuring Service (call to) response time

*/

public class ServiceRequestInterceptor implements ClientHttpRequestInterceptor {

    private List<GroupedUrl> groupedList;

    public ServiceRequestInterceptor(List<GroupedUrl> groupedList) {
        this.groupedList = groupedList;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) {

        ClientHttpResponse response = null;
        try {
            LocalTime start = LocalTime.now();
            response = clientHttpRequestExecution.execute(httpRequest, bytes);
            String regexUrl = toRegexPath(httpRequest.getURI());

            MetricInfo metricInfo = new MetricInfo(regexUrl, response.getRawStatusCode(), Duration.between(start, LocalTime.now()).toMillis());
            MetricPublisherConfig.publish(metricInfo);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return response;
        }
    }

    private String toRegexPath(URI uri) {
        final String fullPath = uri.toString();

        for(GroupedUrl url : groupedList) {
            if(isMatch(fullPath, createRegexForURL(url.getUrl()))) return createRegexForURL(url.getUrl());
        }

        return fullPath;
    }

    private static String createRegexForURL(String path) {
        String regex = "/\\{\\w*\\}";
        return path.replaceAll(regex, "/.*");
    }

    private static boolean isMatch(String path, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(path).matches();
    }
}
