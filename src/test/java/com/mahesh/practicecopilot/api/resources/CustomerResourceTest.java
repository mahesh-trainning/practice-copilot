package com.mahesh.practicecopilot.api.resources;

import com.mahesh.practicecopilot.api.config.ApiApplicationConfig;
import com.mahesh.practicecopilot.api.models.CustomerResourceBean;
import com.mahesh.practicecopilot.api.models.CustomerResourceResponse;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class CustomerResourceTest extends JerseyTest {

    private static final AtomicLong ID_SEQ = new AtomicLong(System.currentTimeMillis());

    @Override
    protected Application configure() {
        return new ApiApplicationConfig();
    }

    @Test
    void getAllCustomers_shouldReturn200AndList() {
        Response response = target("customers/").request().get();

        assertEquals(200, response.getStatus());
        CustomerResourceResponse payload = response.readEntity(CustomerResourceResponse.class);
        assertNotNull(payload);
        assertNotNull(payload.getCustomers());
    }

    @Test
    void getCustomerById_shouldReturn200WhenFound() {
        long id = nextId();
        createCustomer(id, "John Read");

        Response response = target("customers/" + id).request().get();

        assertEquals(200, response.getStatus());
        CustomerResourceBean payload = response.readEntity(CustomerResourceBean.class);
        assertEquals(id, payload.getId());
        assertEquals("John Read", payload.getName());
    }

    @Test
    void getCustomerById_shouldReturn404WhenNotFound() {
        Response response = target("customers/999999999").request().get();

        assertEquals(404, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("Customer not found"));
    }

    @Test
    void createCustomer_shouldReturn201WhenValid() {
        long id = nextId();
        String json = requestJson(id, "John Create", "john.create@example.com", "9876543210", "Hyderabad");

        Response response = target("customers/")
            .request()
            .post(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(201, response.getStatus());
        CustomerResourceBean payload = response.readEntity(CustomerResourceBean.class);
        assertEquals(id, payload.getId());
        assertEquals("John Create", payload.getName());
        assertNotNull(payload.getCreatedDate());
    }

    @Test
    void createCustomer_shouldReturn400WhenRequestBodyMissingWrapper() {
        Response response = target("customers/")
            .request()
            .post(Entity.entity("{}", MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("customerResourceBean is mandatory"));
    }

    @Test
    void createCustomer_shouldReturn400WhenIdMissing() {
        String json = "{" +
            "\"customerResourceBean\":{" +
            "\"name\":\"No Id\"," +
            "\"email\":\"noid@example.com\"" +
            "}" +
            "}";

        Response response = target("customers/")
            .request()
            .post(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("id is mandatory"));
    }

    @Test
    void createCustomer_shouldReturn400WhenNameMissing() {
        long id = nextId();
        String json = requestJson(id, "", "noname@example.com", "9876543210", "Pune");

        Response response = target("customers/")
            .request()
            .post(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("name is mandatory"));
    }

    @Test
    void createCustomer_shouldReturn400WhenEmailInvalid() {
        long id = nextId();
        String json = requestJson(id, "Invalid Email", "invalid-email", "9876543210", "Pune");

        Response response = target("customers/")
            .request()
            .post(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("invalid email format"));
    }

    @Test
    void createCustomer_shouldReturn400WhenPhoneInvalid() {
        long id = nextId();
        String json = requestJson(id, "Invalid Phone", "phone@example.com", "12345", "Pune");

        Response response = target("customers/")
            .request()
            .post(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("invalid phone format"));
    }

    @Test
    void createCustomer_shouldReturn400WhenIdAlreadyExists() {
        long id = nextId();
        createCustomer(id, "First Record");

        String duplicateJson = requestJson(id, "Duplicate Record", "duplicate@example.com", "9876543210", "Mumbai");
        Response response = target("customers/")
            .request()
            .post(Entity.entity(duplicateJson, MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("Customer already exists"));
    }

    @Test
    void updateCustomer_shouldReturn200AndPreserveCreatedDate() {
        long id = nextId();
        CustomerResourceBean created = createCustomer(id, "Update Original");

        String updateJson = requestJson(id, "Update Changed", "update@example.com", "9123456789", "Chennai");
        Response response = target("customers/" + id)
            .request()
            .put(Entity.entity(updateJson, MediaType.APPLICATION_JSON));

        assertEquals(200, response.getStatus());
        CustomerResourceBean updated = response.readEntity(CustomerResourceBean.class);
        assertEquals(id, updated.getId());
        assertEquals("Update Changed", updated.getName());
        assertNotNull(updated.getCreatedDate());
        assertEquals(created.getCreatedDate(), updated.getCreatedDate());
    }

    @Test
    void updateCustomer_shouldReturn400WhenPathIdMismatchesBodyId() {
        long id = nextId();
        long differentId = id + 1;
        createCustomer(id, "Mismatch Seed");

        String updateJson = requestJson(differentId, "Mismatch", "mismatch@example.com", "9123456789", "Delhi");
        Response response = target("customers/" + id)
            .request()
            .put(Entity.entity(updateJson, MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("request id must match path id"));
    }

    @Test
    void updateCustomer_shouldReturn404WhenIdNotFound() {
        long id = nextId();
        String updateJson = requestJson(id, "Not Found", "notfound@example.com", "9123456789", "Delhi");

        Response response = target("customers/" + id)
            .request()
            .put(Entity.entity(updateJson, MediaType.APPLICATION_JSON));

        assertEquals(404, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("Customer not found"));
    }

    @Test
    void deleteCustomer_shouldReturn204WhenFound() {
        long id = nextId();
        createCustomer(id, "Delete Me");

        Response deleteResponse = target("customers/" + id).request().delete();
        assertEquals(204, deleteResponse.getStatus());

        Response getResponse = target("customers/" + id).request().get();
        assertEquals(404, getResponse.getStatus());
    }

    @Test
    void deleteCustomer_shouldReturn404WhenNotFound() {
        Response response = target("customers/999999998").request().delete();

        assertEquals(404, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("Customer not found"));
    }

    private CustomerResourceBean createCustomer(long id, String name) {
        String json = requestJson(id, name, name.toLowerCase().replace(" ", "") + "@example.com", "9876543210", "Bangalore");
        Response response = target("customers/")
            .request()
            .post(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(201, response.getStatus());
        return response.readEntity(CustomerResourceBean.class);
    }

    private long nextId() {
        return ID_SEQ.incrementAndGet();
    }

    private String requestJson(long id, String name, String email, String phone, String address) {
        return "{" +
            "\"customerResourceBean\":{" +
            "\"id\":" + id + "," +
            "\"name\":\"" + name + "\"," +
            "\"email\":\"" + email + "\"," +
            "\"phone\":\"" + phone + "\"," +
            "\"address\":\"" + address + "\"" +
            "}" +
            "}";
    }
}
