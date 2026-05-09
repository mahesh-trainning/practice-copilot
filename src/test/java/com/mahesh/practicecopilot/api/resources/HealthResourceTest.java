package com.mahesh.practicecopilot.api.resources;

import com.mahesh.practicecopilot.api.config.ApiApplicationConfig;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ApiApplicationConfig();
    }

    @Test
    void shouldReturn200ForHealthEndpoint() {
        Response response = target("health").request().get();
        assertEquals(200, response.getStatus());
    }
}
