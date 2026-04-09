package br.com.agibank.qa.api.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.api.client.DogApiClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Dog API")
@Feature("Edge Cases")
class EdgeCaseTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("Breed name with special characters returns 404")
  @Description("Breed names containing @, #, $ should return a 404 error")
  @Severity(SeverityLevel.NORMAL)
  void breedWithSpecialCharactersReturns404() {
    Response response = client.getBreedImages("bull@dog#123");

    assertAll(
        () -> assertEquals(404, response.statusCode(), "Should return 404 for special characters"),
        () ->
            assertEquals(
                "error", response.jsonPath().getString("status"), "Status should be 'error'"));
  }

  @Test
  @DisplayName("Numeric breed name returns 404")
  @Description("Using a numeric value as breed name should return 404")
  @Severity(SeverityLevel.NORMAL)
  void numericBreedNameReturns404() {
    Response response = client.getBreedImages("12345");

    assertEquals(404, response.statusCode(), "Should return 404 for numeric breed");
  }

  @Test
  @DisplayName("Very long breed name returns 404")
  @Description("A breed name exceeding 100 characters should return 404 gracefully")
  @Severity(SeverityLevel.NORMAL)
  void veryLongBreedNameReturns404() {
    String longBreed = "a".repeat(200);
    Response response = client.getBreedImages(longBreed);

    assertTrue(
        response.statusCode() == 404 || response.statusCode() == 414,
        "Should return 404 or 414 for very long breed name, got " + response.statusCode());
  }

  @Test
  @DisplayName("Breed name with unicode characters returns 404")
  @Description("Unicode characters in breed name should be handled without server crash")
  @Severity(SeverityLevel.NORMAL)
  void unicodeBreedNameIsHandled() {
    Response response = client.getBreedImages("café☕🐕");

    assertTrue(
        response.statusCode() < 500,
        "Should not return 5xx for unicode breed name, got " + response.statusCode());
  }

  @Test
  @DisplayName("Multiple random images returns correct count")
  @Description("GET /breeds/image/random/5 should return exactly 5 image URLs")
  @Severity(SeverityLevel.CRITICAL)
  void multipleRandomImagesReturnsCorrectCount() {
    Response response = client.getMultipleRandomImages(5);
    List<String> images = response.jsonPath().getList("message");

    assertAll(
        () -> assertEquals(200, response.statusCode(), "Status code should be 200"),
        () -> assertEquals("success", response.jsonPath().getString("status")),
        () -> assertEquals(5, images.size(), "Should return exactly 5 images"));
  }

  @Test
  @DisplayName("Random image with count zero returns empty or error")
  @Description("Requesting 0 random images should be handled gracefully")
  @Severity(SeverityLevel.NORMAL)
  void randomImagesWithZeroCount() {
    Response response = client.getMultipleRandomImages(0);

    assertTrue(
        response.statusCode() == 200 || response.statusCode() == 400,
        "Should return 200 or 400 for zero count, got " + response.statusCode());
  }

  @Test
  @DisplayName("Random image with negative count is handled")
  @Description("Requesting a negative number of random images should not cause a server error")
  @Severity(SeverityLevel.NORMAL)
  void randomImagesWithNegativeCount() {
    Response response = client.getMultipleRandomImages(-1);

    assertTrue(
        response.statusCode() < 500,
        "Should not return 5xx for negative count, got " + response.statusCode());
  }

  @Test
  @DisplayName("Sub-breed images endpoint returns valid results")
  @Description("GET /breed/hound/afghan/images should return images for the afghan hound sub-breed")
  @Severity(SeverityLevel.CRITICAL)
  void subBreedImagesReturnsResults() {
    Response response = client.getSubBreedImages("hound", "afghan");
    List<String> images = response.jsonPath().getList("message");

    assertAll(
        () -> assertEquals(200, response.statusCode(), "Status code should be 200"),
        () -> assertEquals("success", response.jsonPath().getString("status")),
        () -> assertFalse(images.isEmpty(), "Image list should not be empty"),
        () ->
            assertTrue(
                images.get(0).contains("hound-afghan"), "Image URL should contain sub-breed path"));
  }

  @Test
  @DisplayName("Invalid sub-breed returns 404")
  @Description("GET /breed/hound/invalidsubbreed/images should return 404")
  @Severity(SeverityLevel.NORMAL)
  void invalidSubBreedReturns404() {
    Response response = client.getSubBreedImages("hound", "invalidsubbreed999");

    assertAll(
        () -> assertEquals(404, response.statusCode(), "Status code should be 404"),
        () ->
            assertEquals(
                "error", response.jsonPath().getString("status"), "Status should be 'error'"));
  }
}
