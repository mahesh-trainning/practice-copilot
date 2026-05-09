package com.mahesh.practicecopilot.api.config;

import org.glassfish.jersey.server.ResourceConfig;

public class ApiApplicationConfig extends ResourceConfig {
    public ApiApplicationConfig() {
        packages("com.mahesh.practicecopilot.api.resources");
    }
}
