package com.ascendcorp.spring.serviceresponsetimemetrics.configuration;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;

public class MetricPublisherConfig {

    private static String NAME_RESPONSE_TIME_MS = "spring_response_time_ms";

    private static MetricPublisherConfig instance = new MetricPublisherConfig();
    private static Map<String, MetricInfo> metricInfoMap = new HashMap();

    public MetricPublisherConfig() {
        System.out.println("MetricPublisherConfig created");
    }

    public static void publish(MetricInfo metricInfo) {
        metricInfoMap.put(metricInfo.getEndpoint(), metricInfo);
        ToDoubleFunction x = (value) -> instance.getDurationInMilliSec(metricInfo.getEndpoint());

        Metrics.gauge(NAME_RESPONSE_TIME_MS, Tags.of("status", Integer.toString(metricInfo.getHttpStatus()), "uri", metricInfo.getEndpoint()), instance, x);
    }

    private Long getDurationInMilliSec(String endpoint) {
        return metricInfoMap.get(endpoint).getDurationInMilliSec();
    }
}
