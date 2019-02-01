# spring-service-response-time-metrics
[![Build Status](https://travis-ci.org/ascendcorp/spring-service-response-time-metrics.svg?branch=master)](https://travis-ci.org/ascendcorp/spring-service-response-time-metrics)
[![Apache 2.0](https://img.shields.io/github/license/micrometer-metrics/micrometer.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Purpose
To publish individual service response time metrics

How to use

How to integrate to grafana dashboard
1. Create a new chart panel
2. Use query command below
    avg(spring_response_time_ms{service_name="$application"}) by (status, method, uri)

1. How to add to your service
    1.1 Add dependency

    <dependency>
        <groupId>com.ascendcorp</groupId>
        <artifactId>spring-service-response-time-metrics</artifactId>
        <version>${spring-service-response-time-metrics.version}</version>
    </dependency>
        
Option:
    1.2 Configure with Grouped url
        in case of any part variable url and prefer a single metric for those endpoint 
        configure your Properties file with this

    service-response-time:
      grouped-urls:
        - .*/api/v1/banners/.*
        - .*/cashier-api/payment/sof.*

It works with automatic generated metrics like below.

     # HELP spring_response_time_ms  
     # TYPE spring_response_time_ms gauge
     spring_response_time_ms{method="GET",status="200",uri="http://mock-url/api/v1/banners?showing=before_login,always",} 168.0
     spring_response_time_ms{method="GET",status="200",uri="http://mock-url/internal-api/v1/configuration",} 168.0
     spring_response_time_ms{method="GET",status="200",uri="http://mock-url/auth/v1/trusted-url",} 206.0
     spring_response_time_ms{method="GET",status="200",uri=".*/api/v1/banners/.*",} 114.0




-------------------------------------
_Licensed under [Apache Software License 2.0](https://www.apache.org/licenses/LICENSE-2.0)_
