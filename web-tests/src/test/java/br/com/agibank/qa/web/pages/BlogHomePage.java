package br.com.agibank.qa.web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class BlogHomePage {

  private static final String URL = "https://blogdoagi.com.br/";

  private final Page page;
  private final Locator searchIcon;
  private final Locator searchInput;
  private final Locator searchSubmitButton;

  public BlogHomePage(Page page) {
    this.page = page;
    this.searchIcon = page.locator("a[aria-label='Search icon link']").first();
    this.searchInput = page.locator("input.search-field[name='s']").first();
    this.searchSubmitButton = page.locator("button#search_submit").first();
  }

  @Step("Navigate to Blog do Agi home page")
  public BlogHomePage navigate() {
    page.navigate(URL);
    page.waitForLoadState();
    return this;
  }

  @Step("Search for term: {term}")
  public void searchFor(String term) {
    searchInput.scrollIntoViewIfNeeded();
    searchInput.fill(term);
    searchSubmitButton.click();
    page.waitForLoadState();
  }

  @Step("Verify search icon exists in the page")
  public boolean isSearchIconPresent() {
    return searchIcon.count() > 0;
  }

  @Step("Verify search form is available on the page")
  public boolean isSearchFormAvailable() {
    return searchInput.count() > 0 && searchSubmitButton.count() > 0;
  }

  @Step("Verify search input is visible after scrolling")
  public boolean isSearchInputVisibleAfterScroll() {
    searchInput.scrollIntoViewIfNeeded();
    return searchInput.isVisible();
  }
}
