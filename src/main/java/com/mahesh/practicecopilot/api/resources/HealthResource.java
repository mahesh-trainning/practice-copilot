package com.mahesh.practicecopilot.api.resources;

import com.mahesh.practicecopilot.api.models.HealthResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    @GET
    public Response health() {
        HealthResponse payload = new HealthResponse("UP", "practice-copilot-api");
        return Response.ok(payload).build();
    }
}
