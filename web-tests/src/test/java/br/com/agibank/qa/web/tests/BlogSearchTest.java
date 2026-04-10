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

@Epic("🌐 Blog do Agi")
@Feature("🔍 Busca")
@Owner("rennan")
@Link(name = "Blog do Agi", url = "https://blogdoagi.com.br")
@DisplayName("🔍 Busca — Blog do Agi")
class BlogSearchTest extends BaseTest {

  private BlogHomePage homePage;

  @BeforeEach
  void setUp() {
    homePage = new BlogHomePage(page);
  }

  @Test
  @DisplayName("✅ Busca com termo válido retorna resultados")
  @Description(
      "Ao pesquisar por 'empréstimo', a página deve retornar artigos com títulos e links válidos, "
          + "confirmando que o motor de busca está funcional.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("✅ Busca Válida")
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
  @DisplayName("🚫 Busca com termo inexistente exibe mensagem de 'nada encontrado'")
  @Description(
      "Ao pesquisar um termo que não existe no blog, nenhum artigo deve ser retornado "
          + "e uma mensagem amigável de 'nada foi encontrado' deve ser exibida.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🚫 Resultado Vazio")
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
  @DisplayName("🧩 Componentes de busca estão presentes na página")
  @Description(
      "O ícone de busca e os elementos do formulário de pesquisa devem existir no DOM da página, "
          + "garantindo que o usuário tenha acesso à funcionalidade de busca.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🧩 Componentes de Interface")
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
