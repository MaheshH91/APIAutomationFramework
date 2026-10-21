package com.api.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.OrderService;
import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class AdvancedApiTestingScenariosTest {

    private OrderService orderService;
    private String jwtToken;

    @BeforeClass
    public void setup() {
        orderService = new OrderService();
        AuthService authService = new AuthService();
        Response loginRes = authService.login(new LoginRequest("holkarmahesh", "Raj@1234"));
        jwtToken = loginRes.as(LoginResponse.class).getToken();
    }

    @Test(description = "Verify Query Parameters and Pagination handling")
    public void testGetOrdersWithQueryParams() {
        Response response = orderService.getOrdersWithPagination(1, 10, jwtToken);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("page"), 1);
        Assert.assertEquals(response.jsonPath().getInt("limit"), 10);
        Assert.assertEquals(response.jsonPath().getList("data").size(), 2);
    }

    @Test(description = "Verify Path Parameter retrieval and Response Time SLA")
    public void testGetOrderByIdAndResponseTime() {
        Response response = orderService.getOrderById(101, jwtToken);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("orderId"), 101);
        Assert.assertEquals(response.jsonPath().getString("item"), "Laptop");

        // Response Time SLA Assertion (< 2000 ms)
        Assert.assertTrue(response.getTimeIn(TimeUnit.MILLISECONDS) < 2000, "Response time SLA breached");
    }

    @Test(description = "Verify PATCH operation for partial payload updates")
    public void testPatchOrderPartialUpdate() {
        Map<String, String> patchPayload = Map.of("status", "SHIPPED");
        Response response = orderService.updateOrderStatus(101, patchPayload, jwtToken);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "SHIPPED");
    }

    @Test(description = "Verify DELETE endpoint for record cancellation")
    public void testDeleteOrder() {
        Response response = orderService.cancelOrder(101, jwtToken);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("message"), "Order 101 cancelled successfully");
    }

    @Test(description = "Verify Multipart Form-Data file upload")
    public void testFileUpload() throws IOException {
        File dummyFile = File.createTempFile("invoice_", ".pdf");
        dummyFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(dummyFile)) {
            writer.write("Dummy PDF Invoice Content");
        }

        Response response = orderService.uploadInvoice(101, dummyFile, jwtToken);
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invoice uploaded successfully");
    }

    @Test(description = "Verify Response Headers and Server Metadata")
    public void testResponseHeaders() {
        Response response = orderService.getOrderById(101, jwtToken);

        Assert.assertTrue(response.getHeader("Content-Type").contains("application/json"));
        Assert.assertNotNull(response.getHeader("X-Request-Id"), "Missing trace header");
    }

    @Test(description = "Verify JSON Schema contract validation")
    public void testJsonSchemaValidation() {
        String expectedSchema = "{\n" +
                "  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"orderId\": { \"type\": \"integer\" },\n" +
                "    \"item\": { \"type\": \"string\" },\n" +
                "    \"amount\": { \"type\": \"number\" },\n" +
                "    \"status\": { \"type\": \"string\" }\n" +
                "  },\n" +
                "  \"required\": [\"orderId\", \"item\", \"amount\", \"status\"]\n" +
                "}";

        Response response = orderService.getOrderById(101, jwtToken);
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(expectedSchema));
    }
}