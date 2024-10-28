package com.example.testcontainer.issue.demo;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    private int getStatusCode(HealthEndpoint health) {
        Status status = health.health().getStatus();
        if (Status.UP.equals(status)) {
            return 1;
        }
        return 0;
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsHealths(HealthEndpoint healthEndpoint) {
        return meterRegistry -> {
            meterRegistry.gauge("health", healthEndpoint, this::getStatusCode);
        };
    }

}
