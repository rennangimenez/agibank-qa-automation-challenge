package br.com.agibank.qa.web.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.web.base.BaseTest;
import br.com.agibank.qa.web.fixtures.ExpectedResults;
import br.com.agibank.qa.web.pages.BlogHomePage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Blog do Agi")
@Feature("Smoke Tests")
class BlogSmokeTest extends BaseTest {

  private BlogHomePage homePage;

  @BeforeEach
  void setUp() {
    homePage = new BlogHomePage(page);
  }

  @Test
  @DisplayName("Home page loads with HTTP 200")
  @Description("Navigating to the blog home page should return a successful response")
  @Severity(SeverityLevel.BLOCKER)
  void homePageLoadsSuccessfully() {
    // Act
    homePage.navigate();

    // Assert
    assertAll(
        "Home page load validation",
        () -> assertTrue(homePage.isLoaded(), "Page header should be visible"),
        () ->
            assertTrue(
                page.url().contains(ExpectedResults.BLOG_URL_FRAGMENT_BLOG)
                    && page.url().contains(ExpectedResults.BLOG_URL_FRAGMENT_AGIBANK),
                "URL should point to the Agibank blog (may redirect): " + page.url()),
        () -> assertFalse(page.title().isEmpty(), "Page should have a title"));
  }

  @Test
  @DisplayName("Main navigation is visible")
  @Description("The primary navigation menu should be rendered on the home page")
  @Severity(SeverityLevel.CRITICAL)
  void mainNavigationIsVisible() {
    // Act
    homePage.navigate();
    boolean navPresent = page.locator("nav").first().isVisible();

    // Assert
    assertTrue(navPresent, "Navigation element should be visible");
  }

  @Test
  @DisplayName("Logo is present on the page")
  @Description("The blog logo/brand image should be visible in the header")
  @Severity(SeverityLevel.NORMAL)
  void logoIsPresent() {
    // Act
    homePage.navigate();
    boolean logoPresent =
        page.locator("header img, header svg, .site-logo, .custom-logo").first().count() > 0;

    // Assert
    assertTrue(logoPresent, "Logo element should be present in the header");
  }

  @Test
  @DisplayName("Footer is present on the page")
  @Description("The page footer should be rendered with visible content")
  @Severity(SeverityLevel.NORMAL)
  void footerIsPresent() {
    // Act
    homePage.navigate();
    boolean footerPresent = page.locator("footer").first().isVisible();

    // Assert
    assertTrue(footerPresent, "Footer element should be visible");
  }

  @Test
  @DisplayName("Page contains article content")
  @Description("The home page should display at least one article/post")
  @Severity(SeverityLevel.CRITICAL)
  void pageContainsArticles() {
    // Act
    homePage.navigate();
    int articleCount = page.locator("article").count();

    // Assert
    assertTrue(articleCount > 0, "Home page should contain at least one article");
  }

  @Test
  @DisplayName("No critical JavaScript errors on page load")
  @Description(
      "The home page should load without critical JS runtime errors (resource 404s are excluded)")
  @Severity(SeverityLevel.NORMAL)
  void noCriticalJsErrorsOnLoad() {
    // Arrange
    List<String> jsErrors = new ArrayList<>();
    page.onConsoleMessage(
        msg -> {
          if ("error".equals(msg.type())) {
            String text = msg.text();
            if (!text.contains("Failed to load resource") && !text.contains("404")) {
              jsErrors.add(text);
            }
          }
        });

    // Act
    homePage.navigate();
    page.reload();
    page.waitForLoadState();

    // Assert
    assertTrue(
        jsErrors.isEmpty(),
        "Should have no critical JS errors, but found: " + String.join(", ", jsErrors));
  }
}
