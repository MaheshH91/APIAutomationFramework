package com.api.constants;

public final class Endpoints {
    private Endpoints() {}

    public static final String AUTH_LOGIN = "/api/auth/login";
    public static final String AUTH_SIGNUP = "/api/auth/signup";
    public static final String AUTH_FORGOT_PASSWORD = "/api/auth/forgot-password";
    public static final String AUTH_BASIC = "/api/auth/basic";
    public static final String AUTH_DIGEST = "/api/auth/digest";
    public static final String AUTH_API_KEY_HEADER = "/api/auth/api-key-header";
    public static final String AUTH_API_KEY_QUERY = "/api/auth/api-key-query";
    public static final String AUTH_OAUTH_TOKEN = "/api/oauth/token";
    public static final String AUTH_SIGNATURE = "/api/auth/signature";

    public static final String USERS_PROFILE = "/api/users/profile";
    public static final String SECURE_OAUTH_RESOURCE = "/api/secure/oauth-resource";

    public static final String ORDERS = "/api/orders";
    public static final String ORDERS_BY_ID = "/api/orders/{id}";
    public static final String ORDERS_INVOICE = "/api/orders/{id}/invoice";
}