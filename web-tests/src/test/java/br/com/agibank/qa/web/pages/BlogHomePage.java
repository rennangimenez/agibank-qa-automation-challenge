package br.com.agibank.qa.web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BlogHomePage {

  private static final String BASE_URL = "https://blogdoagi.com.br";

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
    page.navigate(BASE_URL);
    page.waitForLoadState();
    return this;
  }

  @Step("Search for term: {term}")
  public void searchFor(String term) {
    String encoded = URLEncoder.encode(term, StandardCharsets.UTF_8);
    page.navigate(BASE_URL + "/?s=" + encoded);
    page.waitForLoadState();
  }

  @Step("Verify search icon exists in the page DOM")
  public boolean isSearchIconPresent() {
    return searchIcon.count() > 0;
  }

  @Step("Verify search form elements exist in the page DOM")
  public boolean isSearchFormAvailable() {
    return searchInput.count() > 0 && searchSubmitButton.count() > 0;
  }

  @Step("Get the page title")
  public String getPageTitle() {
    return page.title();
  }

  @Step("Verify page loaded successfully")
  public boolean isLoaded() {
    return page.locator("header").first().isVisible();
  }
}
