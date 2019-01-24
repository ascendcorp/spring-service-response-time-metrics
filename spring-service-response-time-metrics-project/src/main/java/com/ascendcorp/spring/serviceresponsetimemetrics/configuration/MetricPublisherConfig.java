package com.ascendcorp.spring.serviceresponsetimemetrics.configuration;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToDoubleFunction;

public class MetricPublisherConfig {

    private static String NAME_RESPONSE_TIME_MS = "spring_response_time_ms";

    private static MetricPublisherConfig instance = new MetricPublisherConfig();
    private static Map<String, MetricInfo> metricInfoMap = new ConcurrentHashMap();

    public MetricPublisherConfig() {
        System.out.println("MetricPublisherConfig created");
    }

    public static void publish(MetricInfo metricInfo) {
        String metricInfoKey = metricInfo.getHttpMethod().toString() + metricInfo.getEndpoint();
        metricInfoMap.put(metricInfoKey, metricInfo);
        ToDoubleFunction referenceMethod = (value) -> instance.getDurationInMilliSec(metricInfoKey);

        Metrics.gauge(NAME_RESPONSE_TIME_MS, Tags.of("status", Integer.toString(metricInfo.getHttpStatus()), "method", metricInfo.getHttpMethod().toString(), "uri", metricInfo.getEndpoint()), instance, referenceMethod);
    }

    private Long getDurationInMilliSec(String key) {
        return metricInfoMap.get(key).getDurationInMilliSec();
    }
}
