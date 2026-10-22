package com.api.utils;

import java.time.Instant;
import com.api.models.request.ProfileRequest;
import com.api.models.request.SignUpRequest;

public final class FakerDataFactory {
    private FakerDataFactory() {}

    public static SignUpRequest createRandomSignUpPayload() {
        long epoch = Instant.now().toEpochMilli();
        return new SignUpRequest.Builder()
                .userName("user_" + epoch)
                .password("SecurePass@" + (epoch % 1000))
                .email("test.automation_" + epoch + "@mocktest.com")
                .firstName("Automation")
                .lastName("Engineer")
                .mobileNumber("9" + String.format("%09d", epoch % 1_000_000_000L))
                .build();
    }

    public static ProfileRequest createRandomProfilePayload() {
        long epoch = Instant.now().toEpochMilli();
        return new ProfileRequest.Builder()
                .firstName("UpdatedFirst_" + (epoch % 100))
                .lastName("UpdatedLast_" + (epoch % 100))
                .email("updated." + epoch + "@mocktest.com")
                .mobileNumber("8" + String.format("%09d", epoch % 1_000_000_000L))
                .build();
    }
}