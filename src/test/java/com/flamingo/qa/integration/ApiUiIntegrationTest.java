package com.flamingo.qa.integration;

import com.flamingo.qa.api.helpers.ApiHelper;
import com.flamingo.qa.api.models.Booking;
import com.flamingo.qa.api.models.BookingResponse;
import com.flamingo.qa.ui.base.BaseUiTest;
import com.flamingo.qa.ui.pages.WebTablePage;
import com.flamingo.qa.ui.pages.WebTablePage.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiUiIntegrationTest extends BaseUiTest {
    private static String token;

    private Integer createdBookingId;

    @BeforeAll
    static void authenticate() {
        token = ApiHelper.getAuthToken();
    }

    @AfterEach
    void deleteCreatedBooking() {
        if (createdBookingId != null) {
            ApiHelper.deleteBooking(createdBookingId, token);
        }
    }

    @Test
    @DisplayName("Booking created through API can be used as test data in Web Tables UI flow")
    void shouldUseApiBookingDataInWebTableUiFlow() {
        Booking booking = new Booking(
                "Api",
                "Integration",
                9500,
                true,
                new Booking.BookingDates("2026-08-01", "2026-08-10"),
                "Automation"
        );

        BookingResponse createdBooking = ApiHelper.createBooking(booking)
                .then()
                .statusCode(200)
                .extract()
                .as(BookingResponse.class);
        createdBookingId = createdBooking.getBookingid();

        Booking retrievedBooking = ApiHelper.getBooking(createdBookingId)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);
        Employee employee = employeeFromBooking(createdBookingId, retrievedBooking);

        WebTablePage webTablePage = new WebTablePage(page)
                .open()
                .addEmployee(employee)
                .searchByEmail(employee.email());

        assertThat(webTablePage.hasEmployee(employee)).isTrue();
    }

    private static Employee employeeFromBooking(int bookingId, Booking booking) {
        return new Employee(
                booking.getFirstname(),
                booking.getLastname(),
                "booking.%d@example.com".formatted(bookingId),
                35,
                booking.getTotalprice(),
                booking.getAdditionalneeds()
        );
    }
}
