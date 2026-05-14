package com.flamingo.qa.config;

public final class TestConfig {
    public static final String RESTFUL_BOOKER_BASE_URL = "https://restful-booker.herokuapp.com";
    public static final String HYGRAPH_GRAPHQL_BASE_URL =
            "https://eu-central-1-shared-euc1-02.cdn.hygraph.com/content/clv3hcefv000001w6a4f3at2l/master";

    public static final String BOOKER_USERNAME = "admin";
    public static final String BOOKER_PASSWORD = "password123";

    public static final String DEMOQA_BASE_URL = "https://demoqa.com";
    public static final String PRACTICE_FORM_URL = DEMOQA_BASE_URL + "/automation-practice-form";
    public static final String WEB_TABLES_URL = DEMOQA_BASE_URL + "/webtables";

    private TestConfig() {
    }
}
