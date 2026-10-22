package com.api.services;

import java.io.File;
import java.util.Map;

import com.api.constants.Endpoints;

import io.restassured.response.Response;

public class OrderService extends BaseService {

    public Response getOrdersWithPagination(int page, int limit, String token) {
        return getRequest(Endpoints.ORDERS, Map.of("page", page, "limit", limit), token);
    }

    public Response getOrderById(int orderId, String token) {
        return getRequest(Endpoints.ORDERS_BY_ID.replace("{id}", String.valueOf(orderId)), token);
    }

    public Response cancelOrder(int orderId, String token) {
        return deleteRequest(Endpoints.ORDERS_BY_ID.replace("{id}", String.valueOf(orderId)), token);
    }

    public Response updateOrderStatus(int orderId, Map<String, String> partialPayload, String token) {
        return patchRequest(partialPayload, Endpoints.ORDERS_BY_ID.replace("{id}", String.valueOf(orderId)), token);
    }

    public Response uploadInvoice(int orderId, File file, String token) {
        return uploadFile(Endpoints.ORDERS_INVOICE.replace("{id}", String.valueOf(orderId)), file, token);
    }
}