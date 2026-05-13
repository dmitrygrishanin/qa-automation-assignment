package com.flamingo.qa.ui.tests;

import com.flamingo.qa.ui.base.BaseUiTest;
import com.flamingo.qa.ui.pages.WebTablePage;
import com.flamingo.qa.ui.pages.WebTablePage.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebTableTest extends BaseUiTest {
    @Test
    @DisplayName("Web table supports employee create, search, update and delete")
    void shouldCreateSearchUpdateAndDeleteEmployee() {
        Employee employee = new Employee(
                "John",
                "Doe",
                "john.doe@example.com",
                30,
                5000,
                "QA"
        );
        Employee updatedEmployee = new Employee(
                "John",
                "Smith",
                "john.smith@example.com",
                31,
                6000,
                "Automation"
        );

        WebTablePage webTablePage = new WebTablePage(page)
                .open()
                .addEmployee(employee)
                .searchByEmail(employee.email());

        assertThat(webTablePage.hasEmployee(employee)).isTrue();

        webTablePage.editEmployee(employee.email(), updatedEmployee)
                .searchByEmail(updatedEmployee.email());

        assertThat(webTablePage.hasEmployee(updatedEmployee)).isTrue();

        webTablePage.deleteEmployee(updatedEmployee.email());

        assertThat(webTablePage.hasRowWithEmail(updatedEmployee.email())).isFalse();
    }
}
