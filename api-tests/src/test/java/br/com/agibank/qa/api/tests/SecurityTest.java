package br.com.agibank.qa.api.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.api.client.DogApiClient;
import br.com.agibank.qa.api.fixtures.SecurityPayloads;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Dog API")
@Feature("Security")
class SecurityTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("SQL injection in breed parameter returns error without exposing database info")
  @Description("Passing SQL payload as breed name should return 404 without database error traces")
  @Severity(SeverityLevel.CRITICAL)
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
  @DisplayName("Path traversal attempt in breed parameter is safely handled")
  @Description("Using ../ sequences should not expose file system paths or server internals")
  @Severity(SeverityLevel.CRITICAL)
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
  @DisplayName("[FINDING] API exposes server technology via X-Powered-By header")
  @Description(
      "Security finding: The API returns X-Powered-By header revealing the backend technology. "
          + "This is a known information disclosure vulnerability that should be mitigated by "
          + "removing or suppressing the header in the server configuration.")
  @Severity(SeverityLevel.NORMAL)
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
  @DisplayName("XSS payload in breed parameter is not reflected in response")
  @Description("Script tags in the breed parameter should not be echoed back in the response body")
  @Severity(SeverityLevel.CRITICAL)
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
  @DisplayName("Malicious Content-Type header is rejected or handled safely")
  @Description("Sending requests with unexpected Content-Type should not cause server errors")
  @Severity(SeverityLevel.NORMAL)
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
  @DisplayName("Oversized request header does not crash the server")
  @Description("Sending an extremely long header value should be rejected gracefully")
  @Severity(SeverityLevel.NORMAL)
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
