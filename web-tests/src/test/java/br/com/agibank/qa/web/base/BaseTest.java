package br.com.agibank.qa.web.base;

import br.com.agibank.qa.web.extensions.ScreenshotOnResultExtension;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ScreenshotOnResultExtension.class)
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
  void createContext(TestInfo testInfo) {
    context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1920, 1080));

    context
        .tracing()
        .start(
            new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setTitle(testInfo.getDisplayName()));

    page = context.newPage();

    ScreenshotOnResultExtension.setPage(page);
    ScreenshotOnResultExtension.setTracingStarted(true);
  }

  @AfterEach
  void closeContext() {
    if (context != null) context.close();
  }
}
