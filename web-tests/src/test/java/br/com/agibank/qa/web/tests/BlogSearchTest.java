package br.com.agibank.qa.web.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.web.base.BaseTest;
import br.com.agibank.qa.web.fixtures.SearchData;
import br.com.agibank.qa.web.pages.BlogHomePage;
import br.com.agibank.qa.web.pages.SearchResultsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Blog do Agi")
@Feature("Search")
@Owner("rennan")
@Link(name = "Blog do Agi", url = "https://blogdoagi.com.br")
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
  @Story("Valid Search")
  void searchWithValidTermReturnsResults() {
    // Arrange
    String searchTerm = SearchData.VALID_TERM;

    // Act
    homePage.navigate();
    homePage.searchFor(searchTerm);
    SearchResultsPage resultsPage = new SearchResultsPage(page);

    // Assert
    assertAll(
        "Search results validation",
        () ->
            assertTrue(resultsPage.urlContainsSearchParam(), "URL should contain search parameter"),
        () -> assertTrue(resultsPage.hasResults(), "Should display at least one result"),
        () ->
            assertTrue(
                resultsPage.getResultsHeadingText().contains(searchTerm),
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
  @Story("Empty Search Results")
  void searchWithNonexistentTermShowsNoResults() {
    // Arrange
    String searchTerm = SearchData.NONEXISTENT_TERM;

    // Act
    homePage.navigate();
    homePage.searchFor(searchTerm);
    SearchResultsPage resultsPage = new SearchResultsPage(page);

    // Assert
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
  @DisplayName("Search components are present on the page")
  @Description("The search icon and form elements should exist in the page DOM")
  @Severity(SeverityLevel.NORMAL)
  @Story("Search UI Components")
  void searchComponentsArePresentOnPage() {
    // Act
    homePage.navigate();

    // Assert
    assertAll(
        "Search UI components validation",
        () -> assertTrue(homePage.isLoaded(), "Page should load successfully"),
        () ->
            assertTrue(homePage.isSearchIconPresent(), "Search icon should be present in the DOM"),
        () ->
            assertTrue(
                homePage.isSearchFormAvailable(), "Search form elements should be in the DOM"));
  }
}
