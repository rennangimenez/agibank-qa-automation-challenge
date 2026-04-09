package br.com.agibank.qa.web.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.web.base.BaseTest;
import br.com.agibank.qa.web.pages.BlogHomePage;
import br.com.agibank.qa.web.pages.SearchResultsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Blog do Agi")
@Feature("Search")
class BlogSearchTest extends BaseTest {

  private BlogHomePage homePage;

  @BeforeEach
  void setUp() {
    homePage = new BlogHomePage(page);
  }

  @Test
  @DisplayName("Search with valid term returns results")
  @Description("Searching for 'empréstimo' should return articles with titles and links")
  @Severity(SeverityLevel.CRITICAL)
  void searchWithValidTermReturnsResults() {
    homePage.navigate().searchFor("empréstimo");

    SearchResultsPage resultsPage = new SearchResultsPage(page);

    assertAll(
        "Search results validation",
        () ->
            assertTrue(resultsPage.urlContainsSearchParam(), "URL should contain search parameter"),
        () -> assertTrue(resultsPage.hasResults(), "Should display at least one result"),
        () ->
            assertTrue(
                resultsPage.getResultsHeadingText().contains("empréstimo"),
                "Heading should contain the search term"),
        () ->
            assertFalse(
                resultsPage.getArticleTitle(0).isEmpty(), "First article should have a title"),
        () -> assertTrue(resultsPage.articleHasLink(0), "First article should have a link"));
  }

  @Test
  @DisplayName("Search with nonexistent term shows no results message")
  @Description(
      "Searching for a nonsense term should display zero articles and a no-results message")
  @Severity(SeverityLevel.NORMAL)
  void searchWithNonexistentTermShowsNoResults() {
    homePage.navigate().searchFor("xyzqwerty999");

    SearchResultsPage resultsPage = new SearchResultsPage(page);

    assertAll(
        "No results validation",
        () ->
            assertTrue(resultsPage.urlContainsSearchParam(), "URL should contain search parameter"),
        () -> assertFalse(resultsPage.hasResults(), "Should not display any results"),
        () ->
            assertTrue(
                resultsPage.hasNoResultsMessage(), "Should display 'nada foi encontrado' message"));
  }

  @Test
  @DisplayName("Search icon and form are present on the page")
  @Description(
      "The search icon should exist in the header and the search form should be accessible")
  @Severity(SeverityLevel.NORMAL)
  void searchComponentsArePresentOnPage() {
    homePage.navigate();

    assertAll(
        "Search UI components validation",
        () ->
            assertTrue(homePage.isSearchIconPresent(), "Search icon should be present in the page"),
        () -> assertTrue(homePage.isSearchFormAvailable(), "Search form should be available"),
        () ->
            assertTrue(
                homePage.isSearchInputVisibleAfterScroll(),
                "Search input should be visible when scrolled into view"));
  }
}
