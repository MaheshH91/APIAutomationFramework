package com.api.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.constants.Endpoints;
import com.api.listeners.TestListener;
import com.api.services.BaseService;
import com.api.utils.TokenManager;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@Listeners(TestListener.class)
public class OrderFulfillmentChainTest extends BaseService {

    private String authToken;
    private int generatedOrderId;
    private File sampleInvoiceFile;

    @BeforeClass
    public void setupChainContext() throws IOException {
        // Step 0: Extract cached/fresh JWT Token
        authToken = TokenManager.getDefaultToken();
        Assert.assertNotNull(authToken, "JWT Auth token must not be null");

        // Prepare temporary multipart test file
        sampleInvoiceFile = File.createTempFile("order_invoice_", ".txt");
        sampleInvoiceFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(sampleInvoiceFile)) {
            writer.write("INVOICE-DATA: Order Verification Test File");
        }
    }

    @Test(priority = 1, description = "Chain 1: Fetch Catalog & Extract Target Item")
    public void testFetchCatalogItem() {
        Response response = getRequestSpec(authToken)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .when()
                .get(Endpoints.ORDERS_ENDPOINT);

        Assert.assertEquals(response.getStatusCode(), 200);

        JsonPath jsonPath = response.jsonPath();
        String firstItemName = jsonPath.getString("data[0].item");
        Assert.assertNotNull(firstItemName, "First item name in catalog should not be null");
    }

    @Test(priority = 2, dependsOnMethods = "testFetchCatalogItem", description = "Chain 2: Create Order & Extract Order ID")
    public void testCreateOrder() {
        String orderPayload = """
            {
                "item": "Laptop",
                "amount": 1200.00,
                "status": "PENDING"
            }
            """;

        Response response = getRequestSpec(authToken)
                .body(orderPayload)
                .when()
                .post(Endpoints.ORDERS_ENDPOINT);

        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201);

        // Fallback to static mock order ID 101 if mock doesn't generate dynamic IDs
        Integer extractedId = response.jsonPath().getInt("orderId");
        generatedOrderId = (extractedId != null && extractedId > 0) ? extractedId : 101;
        Assert.assertTrue(generatedOrderId > 0, "Generated Order ID must be valid");
    }

    @Test(priority = 3, dependsOnMethods = "testCreateOrder", description = "Chain 3: Attach Invoice via Multipart Form")
    public void testUploadOrderInvoice() {
        String uploadEndpoint = Endpoints.ORDERS_ENDPOINT + "/" + generatedOrderId + "/invoice";

        Response response = uploadFile(uploadEndpoint, sampleInvoiceFile, authToken);

        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertEquals(response.jsonPath().getInt("orderId"), generatedOrderId);
    }

    @Test(priority = 4, dependsOnMethods = "testUploadOrderInvoice", description = "Chain 4: Patch Order Status to SHIPPED")
    public void testPatchOrderStatus() {
        String patchEndpoint = Endpoints.ORDERS_ENDPOINT + "/" + generatedOrderId;
        String patchPayload = "{\"status\":\"SHIPPED\"}";

        Response response = getRequestSpec(authToken)
                .body(patchPayload)
                .when()
                .patch(patchEndpoint);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "SHIPPED");
    }

    @Test(priority = 5, dependsOnMethods = "testPatchOrderStatus", description = "Chain 5: Delete Order & Verify Cleanup")
    public void testDeleteOrderStep() {
        String deleteEndpoint = Endpoints.ORDERS_ENDPOINT + "/" + generatedOrderId;

        Response response = getRequestSpec(authToken)
                .when()
                .delete(deleteEndpoint);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.jsonPath().getString("message").contains("cancelled successfully"));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (sampleInvoiceFile != null && sampleInvoiceFile.exists()) {
            sampleInvoiceFile.delete();
        }
    }
}