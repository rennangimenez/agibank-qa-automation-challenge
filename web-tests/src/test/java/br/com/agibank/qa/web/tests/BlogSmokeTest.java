package br.com.agibank.qa.web.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.web.base.BaseTest;
import br.com.agibank.qa.web.fixtures.ExpectedResults;
import br.com.agibank.qa.web.pages.BlogHomePage;
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
@Feature("💨 Smoke Tests")
@Owner("rennan")
@Link(name = "Blog do Agi", url = "https://blogdoagi.com.br")
@DisplayName("💨 Smoke Tests — Blog do Agi")
class BlogSmokeTest extends BaseTest {

  private BlogHomePage homePage;

  @BeforeEach
  void setUp() {
    homePage = new BlogHomePage(page);
  }

  @Test
  @DisplayName("🏠 Página inicial carrega com sucesso")
  @Description(
      "Verifica se a home do blog carrega corretamente retornando HTTP 200, "
          + "com header visível, URL apontando para o domínio correto e título preenchido.")
  @Severity(SeverityLevel.BLOCKER)
  @Story("🏠 Carregamento da Página")
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
  @DisplayName("📌 Menu de navegação principal está visível")
  @Description("O menu de navegação principal deve estar renderizado e visível na home page.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("🏗️ Estrutura da Página")
  void mainNavigationIsVisible() {
    // Act
    homePage.navigate();
    boolean navPresent = page.locator("nav").first().isVisible();

    // Assert
    assertTrue(navPresent, "Navigation element should be visible");
  }

  @Test
  @DisplayName("🎨 Logo está presente no header")
  @Description("A logo/marca do blog deve estar presente e visível no cabeçalho da página.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🏗️ Estrutura da Página")
  void logoIsPresent() {
    // Act
    homePage.navigate();
    boolean logoPresent =
        page.locator("header img, header svg, .site-logo, .custom-logo").first().count() > 0;

    // Assert
    assertTrue(logoPresent, "Logo element should be present in the header");
  }

  @Test
  @DisplayName("📎 Footer está presente na página")
  @Description("O rodapé da página deve estar renderizado com conteúdo visível.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🏗️ Estrutura da Página")
  void footerIsPresent() {
    // Act
    homePage.navigate();
    boolean footerPresent = page.locator("footer").first().isVisible();

    // Assert
    assertTrue(footerPresent, "Footer element should be visible");
  }

  @Test
  @DisplayName("📰 Página exibe artigos/posts")
  @Description("A home page deve exibir pelo menos um artigo ou post publicado.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("📝 Conteúdo")
  void pageContainsArticles() {
    // Act
    homePage.navigate();
    int articleCount = page.locator("article").count();

    // Assert
    assertTrue(articleCount > 0, "Home page should contain at least one article");
  }

  @Test
  @DisplayName("🐛 Sem erros críticos de JavaScript no carregamento")
  @Description(
      "A home page deve carregar sem erros críticos de JS no console. "
          + "Erros de recurso 404 são ignorados por serem comuns em ambientes de produção.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🏠 Carregamento da Página")
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
