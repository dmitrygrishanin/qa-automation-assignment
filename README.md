# QA Automation Assignment

Test automation project for the Flamingo AQA Engineer home assignment.

## Tech Stack

- Java 17
- Maven
- JUnit 6
- REST Assured
- Playwright Java
- AssertJ
- Jackson
- Allure Report

## Project Structure

```text
src/test/java/com/flamingo/qa
├── api
│   ├── helpers
│   ├── models
│   └── tests
├── config
├── integration
└── ui
    ├── base
    ├── pages
    └── tests

src/test/resources
├── img.png
└── schemas
```

## Covered Scenarios

### Part 1: API Tests

RESTful Booker coverage:

- authentication with valid and invalid credentials
- create booking
- retrieve booking
- update booking
- delete booking
- retrieve booking ids
- filter bookings by name
- filter bookings by dates
- negative checks for unauthorized update and missing booking
- JSON schema validation
- basic field validation

### Part 2: UI Tests

Option A: Form + Web Tables.

Practice Form:

- fill student registration form
- upload file from `src/test/resources/img.png`
- select date from date picker
- choose values from dropdown/autocomplete controls
- submit form
- verify success modal values

Web Tables:

- add employee record
- search employee by email
- verify row values
- edit employee record
- verify updated row values
- delete employee record
- verify deleted row is absent

### Integration Tests

API + UI coverage:

- create booking through RESTful Booker API
- retrieve created booking through API
- map API booking data to Web Tables employee data
- add mapped data through Web Tables UI
- verify UI table contains data prepared through API
- delete created booking through API cleanup

## Run Tests

Run all tests:

```bash
mvn test
```

## Browser Configuration

UI tests run with Playwright Chromium and Chrome channel by default.

Optional properties:

```bash
-Dbrowser=chromium
-DbrowserChannel=chrome
-Dheadless=true
```

## Test Reports

Allure results are generated after test execution:

```text
target/allure-results
```

Generate Allure HTML report:

```bash
mvn allure:report
```

Open Allure report in browser:

```bash
mvn allure:serve
```
