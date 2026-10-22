package com.api.services;

import java.util.Map;

import com.api.constants.Endpoints;
import com.api.models.request.LoginRequest;
import com.api.models.request.SignUpRequest;

import io.restassured.response.Response;

public class AuthService extends BaseService {

    public Response login(LoginRequest payload) {
        return postRequest(payload, Endpoints.AUTH_LOGIN);
    }

    public Response signUp(SignUpRequest payload) {
        return postRequest(payload, Endpoints.AUTH_SIGNUP);
    }

    public Response forgotPassword(String emailAddress) {
        Map<String, String> payload = Map.of("email", emailAddress);
        return postRequest(payload, Endpoints.AUTH_FORGOT_PASSWORD);
    }
}