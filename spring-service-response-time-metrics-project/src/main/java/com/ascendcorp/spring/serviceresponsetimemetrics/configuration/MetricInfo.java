package com.ascendcorp.spring.serviceresponsetimemetrics.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetricInfo {

    private String endpoint;
    private int httpStatus;
    private long durationInMilliSec;

}
