package br.com.agibank.qa.web.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

  protected static Playwright playwright;
  protected static Browser browser;
  protected BrowserContext context;
  protected Page page;

  @BeforeAll
  static void launchBrowser() {
    playwright = Playwright.create();
    browser =
        playwright
            .chromium()
            .launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(0));
  }

  @AfterAll
  static void closeBrowser() {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @BeforeEach
  void createContext() {
    context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1920, 1080));
    page = context.newPage();
  }

  @AfterEach
  void closeContext() {
    if (context != null) context.close();
  }
}
