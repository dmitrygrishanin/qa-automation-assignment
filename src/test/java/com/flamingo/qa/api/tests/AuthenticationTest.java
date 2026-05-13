package com.flamingo.qa.api.tests;

import com.flamingo.qa.api.helpers.ApiHelper;
import com.flamingo.qa.api.models.AuthResponse;
import com.flamingo.qa.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AuthenticationTest {
    @Test
    @DisplayName("POST /auth returns token for valid credentials")
    void shouldReturnAuthTokenForValidCredentials() {
        AuthResponse authResponse = ApiHelper
                .authenticate(TestConfig.BOOKER_USERNAME, TestConfig.BOOKER_PASSWORD)
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);

        assertThat(authResponse.getToken()).isNotBlank();
    }

    @Test
    @DisplayName("POST /auth returns bad credentials reason for invalid password")
    void shouldReturnBadCredentialsForInvalidPassword() {
        ApiHelper
                .authenticate(TestConfig.BOOKER_USERNAME, "wrong-password")
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
    }
}
