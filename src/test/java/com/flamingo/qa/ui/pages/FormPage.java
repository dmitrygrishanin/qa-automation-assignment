package com.flamingo.qa.ui.pages;

import com.flamingo.qa.config.TestConfig;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Path;
import java.util.List;

public class FormPage {
    private static final String FIRST_NAME_INPUT = "#firstName";
    private static final String LAST_NAME_INPUT = "#lastName";
    private static final String EMAIL_INPUT = "#userEmail";
    private static final String GENDER_LABEL_TEMPLATE = "label[for='gender-radio-%s']";
    private static final String MOBILE_INPUT = "#userNumber";
    private static final String DATE_OF_BIRTH_INPUT = "#dateOfBirthInput";
    private static final String DATE_MONTH_SELECT = ".react-datepicker__month-select";
    private static final String DATE_YEAR_SELECT = ".react-datepicker__year-select";
    private static final String DATE_DAY_TEMPLATE = ".react-datepicker__day--0%s:not(.react-datepicker__day--outside-month)";
    private static final String SUBJECT_INPUT = "#subjectsInput";
    private static final String SUBJECT_OPTION = ".subjects-auto-complete__option";
    private static final String HOBBY_LABEL_TEMPLATE = "label[for='hobbies-checkbox-%s']";
    private static final String UPLOAD_PICTURE_INPUT = "#uploadPicture";
    private static final String CURRENT_ADDRESS_INPUT = "#currentAddress";
    private static final String STATE_DROPDOWN = "#state";
    private static final String STATE_INPUT = "#react-select-3-input";
    private static final String STATE_FIRST_OPTION = "#react-select-3-option-0";
    private static final String CITY_DROPDOWN = "#city";
    private static final String CITY_INPUT = "#react-select-4-input";
    private static final String CITY_FIRST_OPTION = "#react-select-4-option-0";
    private static final String SUBMIT_BUTTON = "#submit";
    private static final String SUBMISSION_MODAL_TITLE = "#example-modal-sizes-title-lg";
    private static final String SUBMISSION_MODAL_ROW = ".modal-body tr";
    private static final String SUBMISSION_MODAL_CELL = "td";

    private final Page page;

    public FormPage(Page page) {
        this.page = page;
    }

    public FormPage open() {
        page.navigate(TestConfig.PRACTICE_FORM_URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        return this;
    }

    public FormPage fillFirstName(String firstName) {
        page.locator(FIRST_NAME_INPUT).fill(firstName);
        return this;
    }

    public FormPage fillLastName(String lastName) {
        page.locator(LAST_NAME_INPUT).fill(lastName);
        return this;
    }

    public FormPage fillEmail(String email) {
        page.locator(EMAIL_INPUT).fill(email);
        return this;
    }

    public FormPage selectGender(Gender gender) {
        page.locator(GENDER_LABEL_TEMPLATE.formatted(gender.controlValue())).click();
        return this;
    }

    public FormPage fillMobile(String mobile) {
        page.locator(MOBILE_INPUT).fill(mobile);
        return this;
    }

    public FormPage selectDateOfBirth(String day, String month, String year) {
        page.locator(DATE_OF_BIRTH_INPUT).click();
        page.locator(DATE_MONTH_SELECT).selectOption(month);
        page.locator(DATE_YEAR_SELECT).selectOption(year);
        page.locator(DATE_DAY_TEMPLATE.formatted(day)).click();
        return this;
    }

    public FormPage selectSubject(String subject) {
        Locator subjectInput = page.locator(SUBJECT_INPUT);
        subjectInput.click();
        subjectInput.fill(subject);
        page.locator(SUBJECT_OPTION, new Page.LocatorOptions()
                        .setHasText(subject))
                .click();
        return this;
    }

    public FormPage selectHobbies(List<Hobby> hobbies) {
        hobbies.forEach(hobby -> page.locator(HOBBY_LABEL_TEMPLATE.formatted(hobby.controlValue())).click());
        return this;
    }

    public FormPage uploadPicture(Path filePath) {
        page.locator(UPLOAD_PICTURE_INPUT).setInputFiles(filePath);
        return this;
    }

    public FormPage fillCurrentAddress(String address) {
        page.locator(CURRENT_ADDRESS_INPUT).fill(address);
        return this;
    }

    public FormPage selectState(String state) {
        page.locator(STATE_DROPDOWN).click();
        page.locator(STATE_INPUT).fill(state);
        page.locator(STATE_FIRST_OPTION, new Page.LocatorOptions()
                        .setHasText(state))
                .click();
        return this;
    }

    public FormPage selectCity(String city) {
        page.locator(CITY_DROPDOWN).click();
        page.locator(CITY_INPUT).fill(city);
        page.locator(CITY_FIRST_OPTION, new Page.LocatorOptions()
                        .setHasText(city))
                .click();
        return this;
    }

    public FormPage submit() {
        Locator submitButton = page.locator(SUBMIT_BUTTON);
        submitButton.click();
        return this;
    }

    public boolean isSubmissionModalVisible() {
        return page.locator(SUBMISSION_MODAL_TITLE).isVisible();
    }

    public String submittedValueFor(String label) {
        return page.locator(SUBMISSION_MODAL_ROW, new Page.LocatorOptions()
                        .setHasText(label))
                .locator(SUBMISSION_MODAL_CELL)
                .nth(1)
                .innerText();
    }

    public enum Gender {
        MALE("Male", "1"),
        FEMALE("Female", "2"),
        OTHER("Other", "3");

        private final String label;
        private final String controlValue;

        Gender(String label, String controlValue) {
            this.label = label;
            this.controlValue = controlValue;
        }

        public String label() {
            return label;
        }

        private String controlValue() {
            return controlValue;
        }
    }

    public enum Hobby {
        SPORTS("Sports", "1"),
        READING("Reading", "2"),
        MUSIC("Music", "3");

        private final String label;
        private final String controlValue;

        Hobby(String label, String controlValue) {
            this.label = label;
            this.controlValue = controlValue;
        }

        public String label() {
            return label;
        }

        private String controlValue() {
            return controlValue;
        }
    }
}
