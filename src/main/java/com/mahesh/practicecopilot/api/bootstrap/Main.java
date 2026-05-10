package com.mahesh.practicecopilot.api.bootstrap;

import com.mahesh.practicecopilot.api.config.ApiApplicationConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;

import jakarta.ws.rs.core.UriBuilder;

public class Main {
    //public static final String BASE_URI = "http://0.0.0.0:8080/api/";
    public static final URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).path("api/").build();

    public static HttpServer startServer() {
        final ApiApplicationConfig config = new ApiApplicationConfig();
        return GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = startServer();
        System.out.println("JAX-RS API started at " + baseUri.toString());
        System.out.println("Health endpoint: " + baseUri.toString() + "health");
        System.in.read();
        server.shutdownNow();
    }
}
