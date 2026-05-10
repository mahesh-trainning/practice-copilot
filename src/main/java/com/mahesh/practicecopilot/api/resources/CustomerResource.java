package com.mahesh.practicecopilot.api.resources;

import com.mahesh.practicecopilot.api.models.CustomerResourceBean;
import com.mahesh.practicecopilot.api.models.CustomerResourceRequest;
import com.mahesh.practicecopilot.api.models.CustomerResourceResponse;
import com.mahesh.practicecopilot.api.store.CustomerStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private final CustomerStore customer = CustomerStore.getInstance();

    // GET /api/customers
    @GET
    @Path("/")
    public Response getAllCustomers() {
        List<CustomerResourceBean> customers = customer.findAll();
        return Response.ok(new CustomerResourceResponse(customers)).build();
    }

    // GET /api/customers/{id}
    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        Optional<CustomerResourceBean> customerOpt = customer.findById(id);
        if (customerOpt.isPresent()) {
            return Response.ok(customerOpt.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"Customer not found with id: " + id + "\"}")
                .build();
    }

    // POST /api/customers
    @POST
    @Path("/")
    public Response createCustomer(CustomerResourceRequest request) {
        CustomerResourceBean bean = request.getCustomerResourceBean();
        CustomerResourceBean created = customer.create(bean);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    // PUT /api/customers/{id}
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, CustomerResourceRequest request) {
        Optional<CustomerResourceBean> updated = customer.update(id, request.getCustomerResourceBean());
        if (updated.isPresent()) {
            return Response.ok(updated.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"Customer not found with id: " + id + "\"}")
                .build();
    }

    // DELETE /api/customers/{id}
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        if (customer.delete(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"Customer not found with id: " + id + "\"}")
                .build();
    }
}
