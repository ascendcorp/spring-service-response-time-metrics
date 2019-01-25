package com.ascendcorp.spring.serviceresponsetimemetrics.configuration;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
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

    private List<String> groupedUrls;

    public ServiceRequestInterceptor(List<String> groupedUrls) {
        this.groupedUrls = groupedUrls;
    }

    private static String createRegexForURL(String path) {
        String regex = "/\\{\\w*\\}";
        return path.replaceAll(regex, "/.*");
    }

    private static boolean isMatch(String path, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(path).matches();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        LocalTime start = LocalTime.now();

        String regexUrl = toRegexPath(request.getURI());

        try {
            ClientHttpResponse response = clientHttpRequestExecution.execute(request, body);
            publishMetrics(request, start, regexUrl, response.getStatusCode());
            return response;

        } catch (IOException exception) {
            publishMetrics(request, start, regexUrl, HttpStatus.SERVICE_UNAVAILABLE);
            throw exception;

        } catch (Exception exception) {
            publishMetrics(request, start, regexUrl, HttpStatus.INTERNAL_SERVER_ERROR);
            throw exception;
        }

    }

    private void publishMetrics(HttpRequest request, LocalTime start, String regexUrl, HttpStatus statusCode) throws IOException {
        MetricInfo metricInfo = new MetricInfo(request.getMethod(), regexUrl, statusCode.value(), Duration.between(start, LocalTime.now()).toMillis());
        MetricPublisherConfig.publish(metricInfo);
    }

    private String toRegexPath(URI uri) {
        final String fullPath = uri.toString();

        if (groupedUrls == null) return fullPath;

        for (String url : groupedUrls) {
            if (isMatch(fullPath, createRegexForURL(url))) return createRegexForURL(url);
        }

        return fullPath;
    }
}
