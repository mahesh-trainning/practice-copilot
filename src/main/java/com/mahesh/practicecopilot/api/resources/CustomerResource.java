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
import java.util.regex.Pattern;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private final CustomerStore customer = CustomerStore.getInstance();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

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
        String validationError = validateCustomerRequest(request, false, null);
        if (validationError != null) {
            return badRequest(validationError);
        }

        CustomerResourceBean bean = request.getCustomerResourceBean();
        try {
            CustomerResourceBean created = customer.create(bean);
            return Response.status(Response.Status.CREATED)
                    .entity(created)
                    .build();
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }
    }

    // PUT /api/customers/{id}
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, CustomerResourceRequest request) {
        String validationError = validateCustomerRequest(request, true, id);
        if (validationError != null) {
            return badRequest(validationError);
        }

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

    private String validateCustomerRequest(CustomerResourceRequest request, boolean isUpdate, Long pathId) {
        if (request == null || request.getCustomerResourceBean() == null) {
            return "customerResourceBean is mandatory";
        }

        CustomerResourceBean bean = request.getCustomerResourceBean();

        if (bean.getId() == null) {
            return "id is mandatory";
        }

        if (isUpdate && pathId != null && !pathId.equals(bean.getId())) {
            return "request id must match path id";
        }

        if (bean.getName() == null || bean.getName().isBlank()) {
            return "name is mandatory";
        }

        if (bean.getEmail() != null && !bean.getEmail().isBlank() && !EMAIL_PATTERN.matcher(bean.getEmail()).matches()) {
            return "invalid email format";
        }

        if (bean.getPhone() != null && !bean.getPhone().isBlank() && !PHONE_PATTERN.matcher(bean.getPhone()).matches()) {
            return "invalid phone format: phone must contain exactly 10 digits";
        }

        return null;
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"message\":\"" + message + "\"}")
                .build();
    }
}
