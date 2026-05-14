package com.flamingo.qa.api.tests;

import com.flamingo.qa.api.helpers.ApiHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GraphQlApiTest {
    private static final String HTTPS_URL_PATTERN = "^https://.+";

    @Test
    @DisplayName("Hygraph GraphQL returns companies with logo data")
    void shouldReturnCompaniesWithLogoData() {
        Response response = ApiHelper.executeHygraphGraphQl("""
                query CompaniesQuery {
                  companies(first: 3) {
                    id
                    name
                    logo {
                      url
                    }
                  }
                }
                """);

        List<Map<String, Object>> companies = response.jsonPath().getList("data.companies");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("errors")).isNull();
        assertThat(companies).isNotEmpty().hasSizeLessThanOrEqualTo(3);
        companies.forEach(company -> {
            assertThat(company.get("id")).as("company id").isNotNull();
            assertThat(company.get("name")).as("company name").isNotNull();
            assertThat(company).extracting("logo.url")
                    .as("company logo url")
                    .asString()
                    .matches(HTTPS_URL_PATTERN);
        });
    }

    @Test
    @DisplayName("Hygraph GraphQL returns errors for invalid company field")
    void shouldReturnGraphQlErrorsForInvalidField() {
        Response response = ApiHelper.executeHygraphGraphQl("""
                query InvalidCompanyFieldQuery {
                  companies(first: 1) {
                    notExistingField
                  }
                }
                """);

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getList("errors")).isNotEmpty();
        assertThat(response.jsonPath().getString("errors[0].message"))
                .contains("notExistingField")
                .contains("is not defined");
        assertThat((Object) response.jsonPath().get("data")).isNull();
    }
}
