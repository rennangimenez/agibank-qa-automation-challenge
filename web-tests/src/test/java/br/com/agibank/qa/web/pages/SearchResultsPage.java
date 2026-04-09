package br.com.agibank.qa.web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class SearchResultsPage {

  private final Page page;
  private final Locator resultArticles;
  private final Locator resultsHeading;
  private final Locator articleTitles;
  private final Locator noResultsText;

  public SearchResultsPage(Page page) {
    this.page = page;
    this.resultArticles = page.locator("main article");
    this.resultsHeading = page.locator("h1").first();
    this.articleTitles = page.locator("main article h2.entry-title");
    this.noResultsText = page.locator("main p").first();
  }

  @Step("Verify URL contains search parameter")
  public boolean urlContainsSearchParam() {
    return page.url().contains("s=");
  }

  @Step("Get the results heading text")
  public String getResultsHeadingText() {
    return resultsHeading.textContent();
  }

  @Step("Get number of search results")
  public int getResultCount() {
    return resultArticles.count();
  }

  @Step("Verify search results are displayed")
  public boolean hasResults() {
    return resultArticles.count() > 0;
  }

  @Step("Verify no-results message is displayed")
  public boolean hasNoResultsMessage() {
    String text = noResultsText.textContent();
    return text != null && text.contains("nada foi encontrado");
  }

  @Step("Get page title")
  public String getPageTitle() {
    return page.title();
  }

  @Step("Get article title at index {index}")
  public String getArticleTitle(int index) {
    return articleTitles.nth(index).textContent().trim();
  }

  @Step("Verify article at index {index} has a link")
  public boolean articleHasLink(int index) {
    return resultArticles.nth(index).locator("a").count() > 0;
  }
}
