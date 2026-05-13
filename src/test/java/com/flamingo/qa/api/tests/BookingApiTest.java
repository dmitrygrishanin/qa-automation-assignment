package com.flamingo.qa.api.tests;

import com.flamingo.qa.api.helpers.ApiHelper;
import com.flamingo.qa.api.models.Booking;
import com.flamingo.qa.api.models.BookingResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

class BookingApiTest {
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static String token;

    @BeforeAll
    static void setUp() {
        token = ApiHelper.getAuthToken();
    }

    // region Booking API positive scenarios
    @Test
    @DisplayName("POST /booking creates a booking")
    void shouldCreateBooking() {
        Booking expectedBooking = Booking.defaultBooking();
        BookingResponse createdResponse = createBooking(expectedBooking);

        try {
            assertThat(createdResponse.getBookingid()).isPositive();
            assertBookingEquals(createdResponse.getBooking(), expectedBooking);
        } finally {
            deleteBooking(createdResponse.getBookingid());
        }
    }

    @Test
    @DisplayName("GET /booking/{id} returns an existing booking")
    void shouldRetrieveBookingById() {
        Booking expectedBooking = Booking.defaultBooking();
        int bookingId = createBooking(expectedBooking).getBookingid();

        try {
            assertBookingEquals(getBooking(bookingId), expectedBooking);
        } finally {
            deleteBooking(bookingId);
        }
    }

    @Test
    @DisplayName("PUT /booking/{id} updates an existing booking")
    void shouldUpdateBookingById() {
        int bookingId = createBooking(Booking.defaultBooking()).getBookingid();
        Booking expectedBooking = new Booking(
                "Jane",
                "Smith",
                275,
                false,
                new Booking.BookingDates("2026-07-10", "2026-07-15"),
                "Late checkout"
        );

        try {
            assertBookingEquals(updateBooking(bookingId, expectedBooking), expectedBooking);
        } finally {
            deleteBooking(bookingId);
        }
    }

    @Test
    @DisplayName("DELETE /booking/{id} deletes an existing booking")
    void shouldDeleteBookingById() {
        int bookingId = createBooking(Booking.defaultBooking()).getBookingid();

        deleteBooking(bookingId);
        assertBookingIsDeleted(bookingId);
    }


    @Test
    @DisplayName("GET /booking returns booking ids")
    void shouldReturnBookingIds() {
        List<Integer> bookingIds = ApiHelper
                .getBookings()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("bookingid", Integer.class);

        assertThat(bookingIds).isNotEmpty();
        assertThat(bookingIds).allMatch(bookingId -> bookingId > 0);
    }

    @Test
    @DisplayName("GET /booking filters bookings by firstname and lastname")
    void shouldFilterBookingsByName() {
        Booking expectedBooking = new Booking(
                "FilterFirst",
                "FilterLast",
                180,
                true,
                new Booking.BookingDates("2026-08-01", "2026-08-05"),
                "Dinner"
        );
        int bookingId = createBooking(expectedBooking).getBookingid();

        try {
            List<Integer> bookingIds = ApiHelper
                    .getBookingsByName(expectedBooking.getFirstname(), expectedBooking.getLastname())
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getList("bookingid", Integer.class);

            assertThat(bookingIds).contains(bookingId);
        } finally {
            deleteBooking(bookingId);
        }
    }

    @Test
    @DisplayName("GET /booking filters bookings by check-in and check-out dates")
    void shouldFilterBookingsByDates() {
        Booking expectedBooking = new Booking(
                "DateFirst",
                "DateLast",
                220,
                false,
                new Booking.BookingDates("2026-09-09", "2026-09-15"),
                "Parking"
        );
        int bookingId = createBooking(expectedBooking).getBookingid();

        try {
            List<Integer> bookingIds = ApiHelper
                    .getBookingsByDates("2026-09-01", "2026-09-30")
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getList("bookingid", Integer.class);

            assertThat(bookingIds).contains(bookingId);
        } finally {
            deleteBooking(bookingId);
        }
    }
    // endregion

    // region Booking API negative scenarios
    @Test
    @DisplayName("PUT /booking/{id} rejects update without auth token")
    void shouldRejectUpdateWithoutToken() {
        int bookingId = createBooking(Booking.defaultBooking()).getBookingid();
        Booking expectedBooking = Booking.defaultBooking();

        try {
            ApiHelper
                    .updateBookingWithoutToken(bookingId, expectedBooking)
                    .then()
                    .statusCode(403);
        } finally {
            deleteBooking(bookingId);
        }
    }

    @Test
    @DisplayName("GET /booking/{id} returns 404 for non-existent booking")
    void shouldReturnNotFoundForNonExistentBooking() {
        final int NON_EXISTENT_BOOKING_ID = 999_999_999;

        ApiHelper
                .getBooking(NON_EXISTENT_BOOKING_ID)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("POST /booking stores invalid date format as invalid normalized value")
    void shouldStoreInvalidDateFormatAsInvalidNormalizedValue() {
        Booking booking = new Booking(
                "Invalid",
                "Date",
                100,
                true,
                new Booking.BookingDates("not-a-date", "also-not-a-date"),
                "Breakfast"
        );

        BookingResponse createdResponse = ApiHelper
                .createBooking(booking)
                .then()
                .statusCode(200)
                .extract()
                .as(BookingResponse.class);

        try {
            assertThat(createdResponse.getBooking().getBookingdates().getCheckin()).isEqualTo("0NaN-aN-aN");
            assertThat(createdResponse.getBooking().getBookingdates().getCheckout()).isEqualTo("0NaN-aN-aN");
        } finally {
            deleteBooking(createdResponse.getBookingid());
        }
    }
    // endregion

    // region Booking API validation scenarios
    @Test
    @DisplayName("POST /booking response matches booking response schema")
    void shouldMatchCreateBookingResponseSchema() {
        BookingResponse createdResponse = ApiHelper
                .createBooking(Booking.defaultBooking())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-response-schema.json"))
                .extract()
                .as(BookingResponse.class);

        deleteBooking(createdResponse.getBookingid());
    }

    @Test
    @DisplayName("GET /booking/{id} response matches booking schema")
    void shouldMatchGetBookingResponseSchema() {
        int bookingId = createBooking(Booking.defaultBooking()).getBookingid();

        try {
            ApiHelper
                    .getBooking(bookingId)
                    .then()
                    .statusCode(200)
                    .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
        } finally {
            deleteBooking(bookingId);
        }
    }

    @Test
    @DisplayName("GET /booking response matches booking ids schema")
    void shouldMatchBookingIdsResponseSchema() {
        ApiHelper
                .getBookings()
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-ids-schema.json"));
    }

    @Test
    @DisplayName("Booking response contains required fields")
    void shouldContainRequiredBookingFields() {
        int bookingId = createBooking(Booking.defaultBooking()).getBookingid();

        try {
            Booking booking = ApiHelper
                    .getBooking(bookingId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(Booking.class);

            assertThat(booking.getFirstname()).isNotBlank();
            assertThat(booking.getLastname()).isNotBlank();
            assertThat(booking.getTotalprice()).isPositive();
            assertThat(booking.getBookingdates()).isNotNull();
            assertThat(booking.getBookingdates().getCheckin()).isNotBlank();
            assertThat(booking.getBookingdates().getCheckout()).isNotBlank();
            assertThat(booking.getAdditionalneeds()).isNotBlank();
        } finally {
            deleteBooking(bookingId);
        }
    }

    @Test
    @DisplayName("Booking dates use yyyy-MM-dd format")
    void shouldReturnDatesInIsoLocalDateFormat() {
        int bookingId = createBooking(Booking.defaultBooking()).getBookingid();

        try {
            Booking booking = ApiHelper
                    .getBooking(bookingId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(Booking.class);

            assertThat(booking.getBookingdates().getCheckin()).matches("\\d{4}-\\d{2}-\\d{2}");
            assertThat(booking.getBookingdates().getCheckout()).matches("\\d{4}-\\d{2}-\\d{2}");
            assertThat(LocalDate.parse(booking.getBookingdates().getCheckin(), ISO_DATE_FORMATTER)).isNotNull();
            assertThat(LocalDate.parse(booking.getBookingdates().getCheckout(), ISO_DATE_FORMATTER)).isNotNull();
        } finally {
            deleteBooking(bookingId);
        }
    }
    // endregion

    //region Booking API helpers
    private static BookingResponse createBooking(Booking booking) {
        return ApiHelper
                .createBooking(booking)
                .then()
                .statusCode(200)
                .extract()
                .as(BookingResponse.class);
    }

    private static Booking getBooking(int bookingId) {
        return ApiHelper
                .getBooking(bookingId)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);
    }

    private static Booking updateBooking(int bookingId, Booking booking) {
        return ApiHelper
                .updateBooking(bookingId, booking, token)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);
    }

    private static void deleteBooking(int bookingId) {
        ApiHelper
                .deleteBooking(bookingId, token)
                .then()
                .statusCode(201);
    }

    private static void assertBookingIsDeleted(int bookingId) {
        ApiHelper
                .getBooking(bookingId)
                .then()
                .statusCode(404);
    }

    private static void assertBookingEquals(Booking actualBooking, Booking expectedBooking) {
        assertThat(actualBooking.getFirstname()).isEqualTo(expectedBooking.getFirstname());
        assertThat(actualBooking.getLastname()).isEqualTo(expectedBooking.getLastname());
        assertThat(actualBooking.getTotalprice()).isEqualTo(expectedBooking.getTotalprice());
        assertThat(actualBooking.isDepositpaid()).isEqualTo(expectedBooking.isDepositpaid());
        assertThat(actualBooking.getAdditionalneeds()).isEqualTo(expectedBooking.getAdditionalneeds());
        assertThat(actualBooking.getBookingdates().getCheckin())
                .isEqualTo(expectedBooking.getBookingdates().getCheckin());
        assertThat(actualBooking.getBookingdates().getCheckout())
                .isEqualTo(expectedBooking.getBookingdates().getCheckout());
    }
    // endregion
}
