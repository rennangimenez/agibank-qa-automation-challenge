package br.com.agibank.qa.api.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.api.client.DogApiClient;
import br.com.agibank.qa.api.fixtures.SecurityPayloads;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("🐕 Dog API")
@Feature("🛡️ Segurança")
@Owner("rennan")
@Link(name = "Dog API Docs", url = "https://dog.ceo/dog-api/")
@DisplayName("🛡️ Segurança — Dog API")
class SecurityTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("💉 SQL Injection no parâmetro de raça não expõe dados")
  @Description(
      "Ao passar um payload SQL como nome de raça, a API deve retornar 404 "
          + "sem expor informações internas do banco de dados nos traces de erro.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("💉 SQL Injection")
  void sqlInjectionInBreedParam() {
    // Arrange
    String payload = SecurityPayloads.SQL_INJECTION;

    // Act
    Response response = client.getBreedImages(payload);

    // Assert
    assertAll(
        "SQL Injection safety",
        () ->
            assertTrue(
                response.statusCode() == 404 || response.statusCode() == 400,
                "Should return 404 or 400, got " + response.statusCode()),
        () ->
            assertFalse(
                response.body().asString().toLowerCase().contains("sql"),
                "Response should not contain SQL references"),
        () ->
            assertFalse(
                response.body().asString().toLowerCase().contains("syntax"),
                "Response should not contain syntax error details"));
  }

  @Test
  @DisplayName("📂 Tentativa de Path Traversal é bloqueada")
  @Description(
      "Usar sequências ../ no parâmetro de raça não deve expor caminhos do sistema de arquivos "
          + "nem informações internas do servidor.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("📂 Path Traversal")
  void pathTraversalInBreedParam() {
    // Arrange
    String payload = SecurityPayloads.PATH_TRAVERSAL;

    // Act
    Response response = client.getBreedImages(payload);

    // Assert
    assertAll(
        "Path traversal safety",
        () ->
            assertTrue(
                response.statusCode() == 404 || response.statusCode() == 400,
                "Should return 404 or 400, got " + response.statusCode()),
        () ->
            assertFalse(
                response.body().asString().contains("root:"),
                "Response should not contain file system content"),
        () ->
            assertFalse(
                response.body().asString().contains("/etc/"),
                "Response should not expose server paths"));
  }

  @Test
  @DisplayName("⚠️ [ACHADO] API expõe tecnologia via header X-Powered-By")
  @Description(
      "Achado de segurança: A API retorna o header X-Powered-By revelando a tecnologia backend. "
          + "Isso é uma vulnerabilidade de divulgação de informações que deveria ser mitigada "
          + "removendo ou suprimindo o header na configuração do servidor.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🔎 Divulgação de Informações")
  void serverInfoExposedInHeaders() {
    // Act
    Response response = client.listAllBreeds();
    String xPoweredBy = response.header("X-Powered-By");

    // Assert
    assertAll(
        "Server info disclosure findings",
        () -> assertNotNull(xPoweredBy, "X-Powered-By header IS present (documenting finding)"),
        () ->
            assertTrue(
                xPoweredBy != null && xPoweredBy.contains("PHP"),
                "Finding: Server exposes PHP version via X-Powered-By: " + xPoweredBy));
  }

  @Test
  @DisplayName("🚨 Payload XSS não é refletido na resposta")
  @Description(
      "Tags <script> no parâmetro de raça não devem ser ecoadas no corpo da resposta, "
          + "evitando ataques de Cross-Site Scripting.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("🚨 Prevenção XSS")
  void xssPayloadInBreedParam() {
    // Arrange
    String payload = SecurityPayloads.XSS_SCRIPT;

    // Act
    Response response = client.getBreedImages(payload);

    // Assert
    assertAll(
        "XSS reflection check",
        () ->
            assertFalse(
                response.body().asString().contains("<script>"),
                "Response should not reflect script tags"));
  }

  @Test
  @DisplayName("🧪 Content-Type malicioso é tratado com segurança")
  @Description(
      "Enviar requisições com Content-Type inesperado não deve causar erro 5xx no servidor.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🧪 Headers Maliciosos")
  void maliciousContentTypeIsHandled() {
    // Arrange
    String maliciousType = SecurityPayloads.MALICIOUS_CONTENT_TYPE;

    // Act
    Response response = client.getWithCustomHeaders(maliciousType);

    // Assert
    assertTrue(
        response.statusCode() < 500,
        "Server should not return 5xx for malicious Content-Type, got " + response.statusCode());
  }

  @Test
  @DisplayName("📏 Header de tamanho excessivo não derruba o servidor")
  @Description(
      "Enviar um valor de header extremamente longo deve ser rejeitado de forma estável, "
          + "sem causar crash ou erro interno no servidor.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🧪 Headers Maliciosos")
  void oversizedHeaderIsHandled() {
    // Arrange
    String headerValue = SecurityPayloads.oversizedHeaderValue();

    // Act
    Response response = client.getWithCustomHeader("X-Custom-Test", headerValue);

    // Assert
    assertTrue(
        response.statusCode() < 500 || response.statusCode() == 502,
        "Server should handle oversized headers gracefully, got " + response.statusCode());
  }
}
