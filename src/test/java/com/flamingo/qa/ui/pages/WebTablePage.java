package com.flamingo.qa.ui.pages;

import com.flamingo.qa.config.TestConfig;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

public class WebTablePage {
     private final Page page;

    private static final String ADD_BUTTON = "#addNewRecordButton";
    private static final String FIRST_NAME_INPUT = "#firstName";
    private static final String LAST_NAME_INPUT = "#lastName";
    private static final String EMAIL_INPUT = "#userEmail";
    private static final String AGE_INPUT = "#age";
    private static final String SALARY_INPUT = "#salary";
    private static final String DEPARTMENT_INPUT = "#department";
    private static final String SUBMIT_BUTTON = "#submit";
    private static final String SEARCH_INPUT = "#searchBox";
    private static final String TABLE_ROW = "table tbody tr";
    private static final String TABLE_CELL = "td";
    private static final String EDIT_BUTTON = "span[id^='edit-record']";
    private static final String DELETE_BUTTON = "span[id^='delete-record']";

    public WebTablePage(Page page) {
        this.page = page;
    }

    public WebTablePage open() {
        page.navigate(TestConfig.WEB_TABLES_URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        return this;
    }

    public WebTablePage addEmployee(Employee employee) {
        Locator addButton = page.locator(ADD_BUTTON);
        addButton.scrollIntoViewIfNeeded();
        addButton.click();
        fillEmployeeForm(employee);
        submitForm();
        return this;
    }

    public WebTablePage searchByEmail(String email) {
        page.locator(SEARCH_INPUT).fill(email);
        return this;
    }

    public boolean hasEmployee(Employee employee) {
        try {
            Locator row = waitForRowByEmail(employee.email());

            return cellText(row, 0).equals(employee.firstName())
                    && cellText(row, 1).equals(employee.lastName())
                    && cellText(row, 2).equals(String.valueOf(employee.age()))
                    && cellText(row, 3).equals(employee.email())
                    && cellText(row, 4).equals(String.valueOf(employee.salary()))
                    && cellText(row, 5).equals(employee.department());
        } catch (TimeoutError error) {
            return false;
        }
    }

    public WebTablePage editEmployee(String email, Employee updatedEmployee) {
        waitForRowByEmail(email).locator(EDIT_BUTTON).click();
        fillEmployeeForm(updatedEmployee);
        submitForm();
        return this;
    }

    public WebTablePage deleteEmployee(String email) {
        waitForRowByEmail(email).locator(DELETE_BUTTON).click();
        return this;
    }

    public boolean hasRowWithEmail(String email) {
        return rowByEmail(email).isVisible();
    }

    private void fillEmployeeForm(Employee employee) {
        page.locator(FIRST_NAME_INPUT).fill(employee.firstName());
        page.locator(LAST_NAME_INPUT).fill(employee.lastName());
        page.locator(EMAIL_INPUT).fill(employee.email());
        page.locator(AGE_INPUT).fill(String.valueOf(employee.age()));
        page.locator(SALARY_INPUT).fill(String.valueOf(employee.salary()));
        page.locator(DEPARTMENT_INPUT).fill(employee.department());
    }

    private void submitForm() {
        page.locator(SUBMIT_BUTTON).click();
    }

    private Locator rowByEmail(String email) {
        return page.locator(TABLE_ROW, new Page.LocatorOptions()
                        .setHasText(email))
                .first();
    }

    private Locator waitForRowByEmail(String email) {
        Locator row = rowByEmail(email);
        row.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
        return row;
    }

    private static String cellText(Locator row, int index) {
        return row.locator(TABLE_CELL).nth(index).innerText();
    }

    public record Employee(
            String firstName,
            String lastName,
            String email,
            int age,
            int salary,
            String department
    ) {
    }
}
