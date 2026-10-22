package com.api.services;

import com.api.constants.Endpoints;
import com.api.models.request.ProfileRequest;

import io.restassured.response.Response;

public class UserProfileManagementService extends BaseService {

    public Response getProfile(String token) {
        return getRequest(Endpoints.USERS_PROFILE, token);
    }

    public Response updateProfile(String token, ProfileRequest payload) {
        return putRequest(payload, Endpoints.USERS_PROFILE, token);
    }
}