package com.api.services;

import com.api.constants.Endpoints;
import com.api.models.request.LoginRequest;
import com.api.models.request.SignUpRequest;

import io.restassured.response.Response;

public class AuthService extends BaseService {

    /**
     * Authenticates the user credentials to generate a JWT token.
     */
    public Response login(LoginRequest loginRequest) {
        return getRequestSpec()
                .body(loginRequest)
                .when()
                .post(Endpoints.LOGIN_ENDPOINT);
    }

    /**
     * Registers a new user account.
     */
    public Response signUp(SignUpRequest signUpRequest) {
        return getRequestSpec()
                .body(signUpRequest)
                .when()
                .post(Endpoints.SIGNUP_ENDPOINT);
    }

    /**
     * Triggers a forgot password request.
     */
    public Response forgotPassword(String email) {
        return getRequestSpec()
                .body("{\"email\":\"" + email + "\"}")
                .when()
                .post(Endpoints.FORGOT_PASSWORD_ENDPOINT);
    }
}