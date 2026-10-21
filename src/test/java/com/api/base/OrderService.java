package com.api.base;

import java.io.File;
import java.util.Map;
import io.restassured.response.Response;

public class OrderService extends BaseService {
    private static final String ORDERS_PATH = "/api/orders";

    public Response getOrdersWithPagination(int page, int limit, String token) {
        return getRequestWithQueryParams(ORDERS_PATH, Map.of("page", page, "limit", limit), token);
    }

    public Response getOrderById(int orderId, String token) {
        return getRequest(ORDERS_PATH + "/" + orderId, token);
    }

    public Response cancelOrder(int orderId, String token) {
        return deleteRequest(ORDERS_PATH + "/" + orderId, token);
    }

    public Response updateOrderStatus(int orderId, Map<String, String> partialPayload, String token) {
        return patchRequest(partialPayload, ORDERS_PATH + "/" + orderId, token);
    }

    public Response uploadInvoice(int orderId, File file, String token) {
        return uploadFile(ORDERS_PATH + "/" + orderId + "/invoice", file, token);
    }
}