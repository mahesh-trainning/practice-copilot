package com.mahesh.practicecopilot.api.resources;

import com.mahesh.practicecopilot.api.models.CustomerResourceBean;
import com.mahesh.practicecopilot.api.models.CustomerResourceRequest;
import com.mahesh.practicecopilot.api.models.CustomerResourceResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {
    
    private static List<CustomerResourceBean> customers = new ArrayList<>();
    private static Long nextId = 1L;
    
    @GET
    @Path("/")
    public Response getAllCustomers() {
        CustomerResourceResponse response = new CustomerResourceResponse(customers);
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        Optional<CustomerResourceBean> customer = customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
        
        if (customer.isPresent()) {
            CustomerResourceResponse response = new CustomerResourceResponse(List.of(customer.get()));
            return Response.ok(response).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Customer not found with id: " + id)
                .build();
    }
    
    @POST
    @Path("/")
    public Response createCustomer(CustomerResourceRequest request) {
        CustomerResourceBean customerBean = request.getCustomerResourceBean();
        customerBean.setId(nextId++);
        customers.add(customerBean);
        
        CustomerResourceResponse response = new CustomerResourceResponse(List.of(customerBean));
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, CustomerResourceRequest request) {
        Optional<CustomerResourceBean> existingCustomer = customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
        
        if (existingCustomer.isPresent()) {
            CustomerResourceBean customerBean = request.getCustomerResourceBean();
            customerBean.setId(id);
            customers.remove(existingCustomer.get());
            customers.add(customerBean);
            
            CustomerResourceResponse response = new CustomerResourceResponse(List.of(customerBean));
            return Response.ok(response).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Customer not found with id: " + id)
                .build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        Optional<CustomerResourceBean> customer = customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
        
        if (customer.isPresent()) {
            customers.remove(customer.get());
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Customer not found with id: " + id)
                .build();
    }
}
