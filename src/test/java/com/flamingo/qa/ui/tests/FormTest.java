package com.flamingo.qa.ui.tests;

import com.flamingo.qa.ui.base.BaseUiTest;
import com.flamingo.qa.ui.pages.FormPage;
import com.flamingo.qa.ui.pages.FormPage.Gender;
import com.flamingo.qa.ui.pages.FormPage.Hobby;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FormTest extends BaseUiTest {
    @Test
    @DisplayName("Practice form can be submitted with filled student registration data")
    void shouldSubmitPracticeFormWithRequiredFields() {
        Path picturePath = Path.of("src/test/resources/img.png").toAbsolutePath();

        FormPage formPage = new FormPage(page)
                .open()
                .fillFirstName("John")
                .fillLastName("Doe")
                .fillEmail("john.doe@example.com")
                .selectGender(Gender.MALE)
                .fillMobile("1234567890")
                .selectDateOfBirth("13", "May", "1990")
                .selectSubject("Maths")
                .selectHobbies(List.of(Hobby.SPORTS))
                .uploadPicture(picturePath)
                .fillCurrentAddress("Main Street 1")
                .selectState("NCR")
                .selectCity("Delhi")
                .submit();

        assertThat(formPage.isSubmissionModalVisible()).isTrue();
        assertThat(formPage.submittedValueFor("Student Name")).isEqualTo("John Doe");
        assertThat(formPage.submittedValueFor("Student Email")).isEqualTo("john.doe@example.com");
        assertThat(formPage.submittedValueFor("Gender")).isEqualTo(Gender.MALE.label());
        assertThat(formPage.submittedValueFor("Mobile")).isEqualTo("1234567890");
        assertThat(formPage.submittedValueFor("Date of Birth")).isEqualTo("13 May,1990");
        assertThat(formPage.submittedValueFor("Subjects")).isEqualTo("Maths");
        assertThat(formPage.submittedValueFor("Hobbies")).isEqualTo(Hobby.SPORTS.label());
        assertThat(formPage.submittedValueFor("Picture")).isEqualTo("img.png");
        assertThat(formPage.submittedValueFor("Address")).isEqualTo("Main Street 1");
        assertThat(formPage.submittedValueFor("State and City")).isEqualTo("NCR Delhi");
    }
}
