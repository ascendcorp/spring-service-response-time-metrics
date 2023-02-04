# spring-service-response-time-metrics
[![Build Status](https://travis-ci.org/ascendcorp/spring-service-response-time-metrics.svg?branch=master)](https://travis-ci.org/ascendcorp/spring-service-response-time-metrics)
[![Apache 2.0](https://img.shields.io/github/license/micrometer-metrics/micrometer.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Purpose
To publish individual service response time metrics

## How to use

## 1. Add dependency
```
    <dependency>
        <groupId>com.ascendcorp</groupId>
        <artifactId>spring-service-response-time-metrics</artifactId>
        <version>${spring-service-response-time-metrics.version}</version>
    </dependency>
```        
## 2. Configures
### A. service-response-time

in case of any part variable url and prefer a single metric for those endpoint configure your properties file with this

```
    service-response-time:
      enabled: true  
      grouped-urls:
        - .*/api/v1/banners/.*
        - .*/cashier-api/payment/sof.*
```


It works with automatic generated metrics like below.
```
     # HELP spring_response_time_ms  
     # TYPE spring_response_time_ms gauge
     spring_response_time_ms{method="GET",status="200",uri="http://mock-url/api/v1/banners?showing=before_login,always",} 168.0
     spring_response_time_ms{method="GET",status="200",uri="http://mock-url/internal-api/v1/configuration",} 168.0
     spring_response_time_ms{method="GET",status="200",uri="http://mock-url/auth/v1/trusted-url",} 206.0
     spring_response_time_ms{method="GET",status="200",uri=".*/api/v1/banners/.*",} 114.0
```

### B. third-party-health-checker

in case of checking health for any third-party service


```
    third-party-health-checker:
      main-status-aggregated: false
      endpoints:
        - name: otherservice
          url: https://ip-address/health
          interval: 10000
```

It works with your health path as below.

```
{
    "status": "UP",
    "components": {
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 499963174912,
                "free": 369876262912,
                "threshold": 10485760,
                "exists": true
            }
        },
        "otherservice": {
            "status": "UP",
            "details": {
                "status": "UP",
                "detail": {
                    "body": null,
                    "url": "https://ip-address/health"
                }
            }
        },
        "ping": {
            "status": "UP"
        }
    }
}
```

It is easy to custom your any notification method with implementing HealthNotification service.

```

@Service
public class NotificationService implements HealthNotification {
    @Override
    public void push(String message) {
        // any kind of messenger application such as Google-chat, Line, etc.
        lineNotifyClient.notify(message);
    }
}

```

-------------------------------------
_Licensed under [Apache Software License 2.0](https://www.apache.org/licenses/LICENSE-2.0)_
