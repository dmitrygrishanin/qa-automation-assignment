package com.flamingo.qa.api.helpers;

import com.flamingo.qa.api.models.AuthResponse;
import com.flamingo.qa.api.models.Booking;
import com.flamingo.qa.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public final class ApiHelper {
    private ApiHelper() {
    }

    public static RequestSpecification restfulBookerRequest() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.RESTFUL_BOOKER_BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .build();
    }

    public static RequestSpecification hygraphGraphQlRequest() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.HYGRAPH_GRAPHQL_BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .build();
    }

    public static String getAuthToken() {
        return authenticate(TestConfig.BOOKER_USERNAME, TestConfig.BOOKER_PASSWORD)
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class)
                .getToken();
    }

    public static Response authenticate(String username, String password) {
        return RestAssured
                .given()
                .spec(restfulBookerRequest())
                .body(Map.of("username", username, "password", password))
                .when()
                .post("/auth");
    }

    public static Response createBooking(Booking booking) {
        return RestAssured
                .given()
                .spec(restfulBookerRequest())
                .body(booking)
                .when()
                .post("/booking");
    }

    public static Response getBooking(int bookingId) {
        return RestAssured
                .given()
                .spec(restfulBookerRequest())
                .when()
                .get("/booking/{id}", bookingId);
    }

    public static Response updateBooking(int bookingId, Booking booking, String token) {
        return RestAssured
                .given()
                .spec(restfulBookerRequest())
                .cookie("token", token)
                .body(booking)
                .when()
                .put("/booking/{id}", bookingId);
    }

    public static Response updateBookingWithoutToken(int bookingId, Booking booking) {
        return RestAssured
                .given()
                .spec(restfulBookerRequest())
                .body(booking)
                .when()
                .put("/booking/{id}", bookingId);
    }

    public static Response deleteBooking(int bookingId, String token) {
        return RestAssured
                .given()
                .spec(restfulBookerRequest())
                .cookie("token", token)
                .when()
                .delete("/booking/{id}", bookingId);
    }

    public static Response getBookings() {
        return RestAssured
                .given()
                .spec(restfulBookerRequest())
                .when()
                .get("/booking");
    }

    public static Response getBookingsByName(String firstname, String lastname) {
        return RestAssured
                .given()
                .spec(restfulBookerRequest())
                .queryParam("firstname", firstname)
                .queryParam("lastname", lastname)
                .when()
                .get("/booking");
    }

    public static Response getBookingsByDates(String checkin, String checkout) {
        return RestAssured
                .given()
                .spec(restfulBookerRequest())
                .queryParam("checkin", checkin)
                .queryParam("checkout", checkout)
                .when()
                .get("/booking");
    }

    public static Response executeHygraphGraphQl(String query) {
        return executeHygraphGraphQl(query, Map.of());
    }

    public static Response executeHygraphGraphQl(String query, Map<String, Object> variables) {
        return RestAssured
                .given()
                .spec(hygraphGraphQlRequest())
                .body(Map.of("query", query, "variables", variables))
                .when()
                .post();
    }
}
