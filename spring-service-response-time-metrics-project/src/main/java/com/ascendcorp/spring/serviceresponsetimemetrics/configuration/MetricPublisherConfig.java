package com.ascendcorp.spring.serviceresponsetimemetrics.configuration;


import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

public class MetricPublisherConfig {

    private static String NAME_RESPONSE_TIME_MS = "spring_response_time_ms";

    private static MetricPublisherConfig instance = new MetricPublisherConfig();

    private static MetricInfo metricInfo;

    public MetricPublisherConfig() {
        System.out.println("MetricPublisherConfig created");
    }

    public static void publish(MetricInfo metricInfo) {
        MetricPublisherConfig.metricInfo = metricInfo;

        Metrics.gauge(NAME_RESPONSE_TIME_MS, Tags.of("status", Integer.toString(metricInfo.getHttpStatus()), "uri", metricInfo.getEndpoint()), instance, MetricPublisherConfig::getDurationInMilliSec);
    }

    private long getDurationInMilliSec() {
        return MetricPublisherConfig.metricInfo.getDurationInMilliSec();
    }
}
