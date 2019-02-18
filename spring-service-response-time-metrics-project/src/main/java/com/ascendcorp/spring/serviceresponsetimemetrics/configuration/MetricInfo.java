package com.ascendcorp.spring.serviceresponsetimemetrics.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetricInfo {

    private HttpMethod httpMethod;
    private String endpoint;
    private int httpStatusCode;
    private long durationInMilliSec;

}
