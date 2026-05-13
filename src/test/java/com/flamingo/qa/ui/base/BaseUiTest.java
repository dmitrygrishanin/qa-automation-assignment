package com.flamingo.qa.ui.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseUiTest {
    protected Page page;

    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = browserType().launch(new BrowserType.LaunchOptions()
                .setChannel(browserChannel())
                .setHeadless(Boolean.parseBoolean(System.getProperty("headless", "true"))));
    }

    @BeforeEach
    void createContext() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1440, 900));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    private static BrowserType browserType() {
        String browserName = System.getProperty("browser", "chromium").toLowerCase();
        return switch (browserName) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            case "chromium" -> playwright.chromium();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browserName);
        };
    }

    private static String browserChannel() {
        String browserName = System.getProperty("browser", "chromium").toLowerCase();
        return "chromium".equals(browserName)
                ? System.getProperty("browserChannel", "chrome")
                : null;
    }
}
