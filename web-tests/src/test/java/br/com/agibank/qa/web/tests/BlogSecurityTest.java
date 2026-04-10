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
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("🌐 Blog do Agi")
@Feature("🛡️ Segurança")
@Owner("rennan")
@Link(name = "Blog do Agi", url = "https://blogdoagi.com.br")
@DisplayName("🛡️ Segurança — Blog do Agi")
class BlogSecurityTest extends BaseTest {

  private BlogHomePage homePage;

  @BeforeEach
  void setUp() {
    homePage = new BlogHomePage(page);
  }

  @Test
  @DisplayName("💉 SQL Injection no campo de busca é tratado com segurança")
  @Description(
      "Ao submeter um payload de SQL injection no campo de busca, a aplicação não deve "
          + "expor erros de banco de dados nem retornar dados sensíveis.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("💉 SQL Injection")
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
  @DisplayName("🚨 Tentativa de XSS não executa scripts")
  @Description(
      "Ao submeter uma tag <script> no campo de busca, o JavaScript não deve ser executado. "
          + "O termo pode aparecer no código-fonte (HTML-encoded), mas o script nunca deve rodar.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("🚨 Prevenção XSS")
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
  @DisplayName("⚠️ [ACHADO] Injeção HTML renderiza elementos no DOM")
  @Description(
      "Achado de segurança: Tags HTML submetidas via busca SÃO renderizadas como elementos reais "
          + "no DOM. O WordPress não sanitiza completamente o HTML no termo pesquisado, "
          + "permitindo que elementos injetados (ex: <h1>) sejam renderizados. "
          + "Isso configura uma vulnerabilidade de HTML injection refletida.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("⚠️ Injeção HTML")
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
  @DisplayName("📏 Input extremamente longo não derruba a aplicação")
  @Description(
      "Ao submeter um termo de busca com 5.000 caracteres, a aplicação deve tratar "
          + "a requisição de forma estável, sem crash ou erro 500.")
  @Severity(SeverityLevel.NORMAL)
  @Story("📏 Limites de Entrada")
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
  @DisplayName("🔣 Caracteres especiais na busca são tratados corretamente")
  @Description(
      "Caracteres como <, >, &, \" e ' devem ser devidamente codificados "
          + "e tratados sem causar erros na aplicação.")
  @Severity(SeverityLevel.NORMAL)
  @Story("📏 Limites de Entrada")
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
