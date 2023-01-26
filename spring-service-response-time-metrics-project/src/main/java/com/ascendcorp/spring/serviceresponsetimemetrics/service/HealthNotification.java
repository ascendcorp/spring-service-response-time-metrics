package com.ascendcorp.spring.serviceresponsetimemetrics.service;

@FunctionalInterface
public interface HealthNotification {

    void push(String message);
}
