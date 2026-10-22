package com.api.services;

import com.api.constants.Endpoints;
import com.api.models.request.ProfileRequest;
import io.restassured.response.Response;

public class UserProfileManagementService extends BaseService {


    /**
     * Retrieves the profile info for the user associated with the provided JWT Bearer token.
     */
    public Response getProfile(String token) {
        return getRequestSpec()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(Endpoints.PROFILE_ENDPOINT);
    }

    /**
     * Updates profile info using the provided JWT Bearer token and payload.
     */
    public Response updateProfile(String token, ProfileRequest payload) {
        return getRequestSpec()
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .when()
                .put(Endpoints.PROFILE_ENDPOINT);
    }
}