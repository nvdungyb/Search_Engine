package com.dzung.search_engine.controller;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Value("${microsoft.azure}")
    private String azureKey;

    @Value("${apiUrl}")
    private String apiUrl;

    @Value("${region}")
    private String region;

    public String getAzureKey() {
        return azureKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getRegion() {
        return region;
    }
}
