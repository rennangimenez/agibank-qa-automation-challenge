package br.com.agibank.qa.web.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.web.base.BaseTest;
import br.com.agibank.qa.web.fixtures.ExpectedResults;
import br.com.agibank.qa.web.fixtures.SearchData;
import br.com.agibank.qa.web.pages.BlogHomePage;
import br.com.agibank.qa.web.pages.SearchResultsPage;
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
@Feature("Security")
class BlogSecurityTest extends BaseTest {

  private BlogHomePage homePage;

  @BeforeEach
  void setUp() {
    homePage = new BlogHomePage(page);
  }

  @Test
  @DisplayName("SQL injection in search field is safely handled")
  @Description("Submitting a SQL injection payload should not cause errors or expose data")
  @Severity(SeverityLevel.CRITICAL)
  void sqlInjectionInSearchIsHandled() {
    // Arrange
    String payload = SearchData.SQL_INJECTION;

    // Act
    homePage.navigate();
    homePage.searchFor(payload);
    SearchResultsPage resultsPage = new SearchResultsPage(page);

    // Assert
    assertAll(
        "SQL Injection safety",
        () -> assertTrue(resultsPage.urlContainsSearchParam(), "URL should contain search param"),
        () -> assertFalse(page.content().contains("SQL"), "Page should not expose SQL errors"),
        () ->
            assertFalse(
                page.content().contains("syntax error"), "Page should not expose syntax errors"),
        () ->
            assertFalse(page.content().contains("mysql"), "Page should not expose database info"));
  }

  @Test
  @DisplayName("XSS attempt in search field does not execute scripts")
  @Description(
      "Submitting a script tag should not trigger JavaScript execution. "
          + "The search term may be reflected in the page source (HTML-encoded), "
          + "but the script must never actually execute.")
  @Severity(SeverityLevel.CRITICAL)
  void xssAttemptDoesNotExecuteScripts() {
    // Arrange
    String payload = SearchData.XSS_SCRIPT;
    List<String> dialogs = new ArrayList<>();
    page.onDialog(
        dialog -> {
          dialogs.add(dialog.message());
          dialog.dismiss();
        });

    // Act
    homePage.navigate();
    homePage.searchFor(payload);

    // Assert
    assertAll(
        "XSS execution prevention",
        () ->
            assertTrue(
                page.url().contains(ExpectedResults.SEARCH_PARAM_KEY),
                "URL should contain search param"),
        () ->
            assertTrue(
                dialogs.isEmpty(),
                "No JavaScript dialogs should have been triggered by XSS payload"));
  }

  @Test
  @DisplayName("[FINDING] HTML injection in search renders as actual DOM elements")
  @Description(
      "Security finding: HTML tags submitted via search ARE rendered as actual DOM elements. "
          + "The WordPress search results page does not fully sanitize HTML in the search term, "
          + "allowing injected elements (e.g., <h1>) to render in the page. "
          + "This is a stored/reflected HTML injection vulnerability.")
  @Severity(SeverityLevel.CRITICAL)
  void htmlInjectionRendersInDom() {
    // Arrange
    String payload = SearchData.HTML_INJECTION;

    // Act
    homePage.navigate();
    homePage.searchFor(payload);
    SearchResultsPage resultsPage = new SearchResultsPage(page);
    int injectedH1Count = page.locator(ExpectedResults.INJECTED_H1_SELECTOR).count();

    // Assert
    assertAll(
        "HTML Injection finding documentation",
        () -> assertTrue(resultsPage.urlContainsSearchParam(), "URL should contain search param"),
        () ->
            assertTrue(
                injectedH1Count > 0,
                "Finding: injected <h1> renders as actual DOM element (HTML injection confirmed)"));
  }

  @Test
  @DisplayName("Extremely long search input does not crash the application")
  @Description("Submitting a 5000-character search term should be handled gracefully")
  @Severity(SeverityLevel.NORMAL)
  void extremelyLongSearchInputIsHandled() {
    // Arrange
    String payload = SearchData.longInput();

    // Act
    homePage.navigate();
    homePage.searchFor(payload);

    // Assert
    assertAll(
        "Long input handling",
        () -> assertNotNull(page.title(), "Page should still have a title"),
        () -> assertTrue(page.locator("body").isVisible(), "Page body should be visible"));
  }

  @Test
  @DisplayName("Search with special characters does not cause errors")
  @Description("Characters like <, >, &, \", ' should be properly encoded and handled")
  @Severity(SeverityLevel.NORMAL)
  void specialCharactersInSearchAreHandled() {
    // Arrange
    String payload = SearchData.SPECIAL_CHARACTERS;

    // Act
    homePage.navigate();
    homePage.searchFor(payload);
    SearchResultsPage resultsPage = new SearchResultsPage(page);

    // Assert
    assertAll(
        "Special characters handling",
        () -> assertTrue(resultsPage.urlContainsSearchParam(), "URL should contain search param"),
        () -> assertNotNull(page.title(), "Page should still render"));
  }
}
